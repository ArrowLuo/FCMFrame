package cartesian;

public class Shunting {
    
    private int precedence(char op) {
        switch (op) {
        case '/': case '*': case '%':
            return 3;
        case '+': case '-':
            return 2;
        }
        return 0;
    }
    
    
    
    private String shunting(String input) {
        StringBuffer sb = new StringBuffer();
        return "";
    }
}