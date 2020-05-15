package online.wgear.test.spring_boot_lezhnin.rest.error;

public class LoopingErrorException extends RuntimeException {
    public LoopingErrorException() {
        super("Looping error");
    }
}
