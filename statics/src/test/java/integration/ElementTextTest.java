package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.PatternSyntaxException;

import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ElementTextTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_divs.html");
  }

  @Test
  void canCheckTextOfElement() {
    $("#child_div1").shouldHave(text("Son"));
    $("#grandchild_div").shouldHave(exactText("Granddaughter"));
  }
  @Test
  void canCheckTextOfElement_partial() {
    Configuration.textCheck = PARTIAL_TEXT;
    $("#child_div1").shouldHave(text("Son"));
    $("#grandchild_div").shouldHave(exactText("Granddaughter"));
  }

  @Test
  void canGetTextOfElementWithChildren() {
    $("#parent_div").shouldHave(exactText("Big papa\n" +
      "Son\n" +
      "Daughter\n" +
      "Granddaughter"));
  }

  @Test
  void canGetTextOfElementWithChildren_partial() {
    $("#parent_div").shouldHave(partialText("g papa\n" +
      "Son\n" +
      "Daughter\n" +
      "Grand"));
  }

  @Test
  void canGetTextOfElementWithoutChildren() {
    assertThat($("#child_div1").getOwnText()).isEqualTo("Son");
    assertThat($("#child_div2").getOwnText()).isEqualTo("Daughter  \n\n  ");
    assertThat($("#parent_div").getOwnText()).isEqualTo("\n" +
      "  Big papa\n" +
      "  \n" +
      "\n" +
      "  \n" +
      "\n");
  }

  @Test
  void canCheckTextOfElementWithoutChildren() {
    $("#child_div1").shouldHave(ownText("Son"));
    $("#child_div2").shouldHave(ownText("Daughter"));
    $("#parent_div").shouldHave(ownText("Big papa"));
    $("#parent_div").shouldNotHave(ownText("papa"));
    $("#parent_div").shouldNotHave(ownText("Son"));
    $("#parent_div").shouldNotHave(ownText("Daughter"));
  }

  @Test
  void canCheckTextOfElementWithoutChildren_partial() {
    Configuration.textCheck = PARTIAL_TEXT;
    $("#child_div1").shouldHave(ownText("So"));
    $("#child_div2").shouldHave(ownText("Daugh"));
    $("#parent_div").shouldHave(ownText("ig pap"));
    $("#parent_div").shouldNotHave(ownText("on"));
    $("#parent_div").shouldNotHave(ownText("aughte"));
  }

  @Test
  void canCheckExactTextOfElementWithoutChildren() {
    $("#child_div1").shouldHave(exactOwnText("Son"));
    $("#child_div2").shouldHave(exactOwnText("Daughter"));
    $("#parent_div").shouldHave(exactOwnText("Big papa"));
    $("#parent_div").shouldNotHave(exactOwnText("papa"));
    $("#parent_div").shouldNotHave(exactOwnText("Son"));
    $("#parent_div").shouldNotHave(exactOwnText("Daughter"));
  }

  @Test
  void canCheckTextByRegularExpression() {
    $("#child_div1").should(matchText("Son"));
    $("#child_div1").should(matchText("S.n"));
    $("#child_div1").should(matchText("So.+"));
    $("#child_div1").should(matchText("So\\w"));
    $("#child_div1").should(matchText("S\\wn"));
  }

  @Test
  void canCheckTextByRegularExpression_partial() {
    Configuration.textCheck = PARTIAL_TEXT;
    $("#child_div1").should(matchText("So"));
    $("#child_div1").should(matchText("S."));
    $("#child_div1").should(matchText("\\wn"));
  }

  @Test
  void cannotUseEmptyRegularExpression() {
    assertThatThrownBy(() -> $("#child_div1").should(matchText("")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Argument must not be null or empty string");
  }

  @Test
  void cannotUseInvalidRegularExpression() {
    assertThatThrownBy(() -> $("#child_div1").should(matchText("{")))
      .isInstanceOf(PatternSyntaxException.class);
  }
}
