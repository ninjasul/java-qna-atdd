package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    public List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    private Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    private Question(String title, String contents, User login) {
        this.title = title;
        this.contents = contents;
        this.writeBy(login);
    }

    private Question(String title, String contents, User login, List<Answer> answers) {
        this.title = title;
        this.contents = contents;
        this.writer = login;
        this.answers.addAll(answers);
    }

    public static Question of(String title, String contents) {
        return new Question(title, contents);
    }

    public static Question ofUser(String title, String contents, User login) {
        return new Question(title, contents, login);
    }

    public static Question ofList(String title, String contents, User login, List<Answer> answers) {
        return new Question(title, contents, login, answers);
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        if (loginUser == null || loginUser.isGuestUser()) {
            throw new UnAuthorizedException();
        }
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public Question update(User loginUser, Question question) {
        if (!writer.equals(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.title = (question.getTitle());
        this.contents = (question.getContents());
        return this;
    }

    public Question delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("삭제권한없습니다.");
        }
        this.deleted = Boolean.TRUE;
        return this;
    }

    public int getAnswerSize() {
        return answers.size();
    }
}
