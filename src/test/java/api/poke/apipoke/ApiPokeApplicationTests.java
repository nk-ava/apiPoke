package api.poke.apipoke;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ApiPokeApplicationTests {

    @Test
    void contextLoads() {
        List<Integer> list = new ArrayList<>();
        list.add(123);
        list.add(354);
        System.out.println(list.get(7));
    }

}
