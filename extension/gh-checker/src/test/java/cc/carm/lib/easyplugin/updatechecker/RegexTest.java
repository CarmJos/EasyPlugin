package cc.carm.lib.easyplugin.updatechecker;

import org.junit.Test;

public class RegexTest {


    @Test
    public void onTest() {
        System.out.println(GHUpdateChecker.getGithubOwner("https://github.com/CarmJos/EasyPlugin"));
        System.out.println(GHUpdateChecker.getGithubOwner("https://github.com/Ghost-chu/QuickShop-Hikari/pulls"));
        System.out.println(GHUpdateChecker.getGithubOwner("https://github.com/QWQ123/QuickShop"));

    }

}
