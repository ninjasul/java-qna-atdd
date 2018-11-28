package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest extends BaseTest {
    private static final String TITLE = "테스트 타이틀";
    private static final String CONTENTS = "테스트 컨텐츠";
    private static final User NEW_USER = UserTest.newUser("mirrors89");


    public static Question newQuestion(String title, String contents) {
        return newQuestion(title, contents, UserTest.JAVAJIGI);
    }

    public static Question newQuestion(String title, String contents, User user) {
        Question question = new Question(title, contents);
        question.writeBy(user);

        return question;
    }

    @Test
    public void create() {
        User newUser = UserTest.newUser("mirrors89");

        Question question = newQuestion(TITLE, CONTENTS, newUser);

        assertThat(question.getTitle()).isEqualTo(TITLE);
        assertThat(question.getContents()).isEqualTo(CONTENTS);
        assertThat(question.isOwner(newUser)).isEqualTo(true);
        assertThat(question.isDeleted()).isEqualTo(false);
    }

    @Test
    public void update_writer() {
        User newUser = UserTest.newUser("mirrors89");
        Question question = newQuestion(TITLE, CONTENTS, newUser);

        String updateTitle = "수정된 타이틀";
        String updateContents = "수정된 컨텐츠";

        question.update(newUser, newQuestion(updateTitle, updateContents));

        assertThat(question.getTitle()).isEqualTo(updateTitle);
        assertThat(question.getContents()).isEqualTo(updateContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_writer() {
        User anotherUser = UserTest.newUser("anotherUser");

        Question question = newQuestion(TITLE, CONTENTS, NEW_USER);

        String updateTitle = "수정된 타이틀";
        String updateContents = "수정된 컨텐츠";

        question.update(anotherUser, newQuestion(updateTitle, updateContents));
    }

    @Test
    public void delete_writer() throws CannotDeleteException {
        Question question = newQuestion(TITLE, CONTENTS, NEW_USER);

        question.delete(NEW_USER);
        assertThat(question.isDeleted()).isEqualTo(true);
    }


    @Test(expected = CannotDeleteException.class)
    public void delete_not_writer() throws CannotDeleteException {
        User anotherUser = UserTest.newUser("anotherUser");

        Question question = newQuestion(TITLE, CONTENTS, NEW_USER);

        question.delete(anotherUser);
    }
}
