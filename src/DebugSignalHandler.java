import sun.misc.Signal;
import sun.misc.SignalHandler;


//Class to handle signals
public class DebugSignalHandler implements SignalHandler {

    private da_proc proc;

    public DebugSignalHandler(da_proc proc) {
        this.proc = proc;
    }

    public void listenTo(String name) {
        Signal signal = new Signal(name);
        Signal.handle(signal,this);
    }

    public void handle(Signal signal) {
        switch (signal.toString().trim()) {
            case "SIGTERM":
            case "SIGINT":
                proc.stop();
                break;
            case "SIGUSR2":
                proc.usr2Signal();
                break;
                default:
                    System.out.println("Other Signal received");
                    break;
        }
    }
}
