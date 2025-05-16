import org.junit.Test;

public class PageTest {


    @Test
    public void test() {
        System.out.println(maxPage(0, 10));
        System.out.println(maxPage(10, 10));
        System.out.println(maxPage(5, 10));
        System.out.println(maxPage(15, 10));
        System.out.println(maxPage(19, 10));
        System.out.println(maxPage(20, 10));

    }

    int maxPage(int contents, int size) {
        if (contents == 0 || size == 0) return 1;
        return (contents / size) + ((contents % size) == 0 ? 0 : 1);
    }
}
