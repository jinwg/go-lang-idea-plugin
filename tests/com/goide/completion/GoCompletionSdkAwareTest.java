package com.goide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.LightProjectDescriptor;

public class GoCompletionSdkAwareTest extends GoCompletionTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  public void testFormatter() {
    doTestInclude("package main; import . \"fmt\"; type alias <caret>", "Formatter");
  }

  public void testAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Fprintl<caret>}",
                  "package main;\n" +
                  "import \"fmt\"\n" +
                  "func test(){fmt.Fprintln(<caret>)}");
  }

  public void testAutoImportWithAlias() {
    doCheckResult("package main; \n" +
                  "import alias `fmt`\n" +
                  "func test(){Fprintl<caret>}",
                  "package main; \n" +
                  "import alias `fmt`\n" +
                  "func test(){alias.Fprintln()}");
  }

  public void testAutoImportWithDotAlias() {
    doCheckResult("package main; \n" +
                  "import . `fmt`\n" +
                  "func test(){Fprintl<caret>}",
                  "package main; \n" +
                  "import . `fmt`\n" +
                  "func test(){Fprintln()}");
  }

  public void testImportedFunctionsPriority() {
    myFixture.configureByText("a.go", "package main; \n" +
                                      "import `io`\n" +
                                      "func test(){ReadA<caret>}");
    myFixture.completeBasic();
    myFixture.assertPreferredCompletionItems(0, "ReadAtLeast", "ReadAtLeastCustom");
  }

  public void testImportedTypesPriority() {
    myFixture.configureByText("a.go", "package main; \n" +
                                      "import `io`\n" +
                                      "func test(ReadWriteSeeke<caret>){}");
    myFixture.completeBasic();
    myFixture.assertPreferredCompletionItems(0, "ReadWriteSeeker", "ReadWriteSeekerCustom");
  }
  
  public void testDoNothingInsideSelector() {
    doTestVariants(
      "package main\n" +
      "import \"fmt\"\n" +
      "func test(){fmt.Sprintln().<caret>}", CompletionType.BASIC, 1, CheckType.EQUALS
    );
  }

  public void testImports() {
    doTestInclude("package main; import \"<caret>", "fmt", "io");
  }

  public void testCaseInsensitiveTypeConversion() {
    doCheckResult("package main; import \"fmt\"; func test(){fmt.form<caret>}",
                  "package main; import \"fmt\"; func test(){fmt.Formatter(<caret>)}");
  }

  public void testCaseInsensitiveFunction() {
    doCheckResult("package main; import \"fmt\"; func test(){fmt.err<caret>}",
                  "package main; import \"fmt\"; func test(){fmt.Errorf(<caret>)}");
  }

  public void testCaseInsensitiveType() {
    doCheckResult("package main; import \"fmt\"; func test(fmt.form<caret>}",
                  "package main; import \"fmt\"; func test(fmt.Formatter<caret>}");
  }
}
