import javax.sound.midi.*;
import java.util.Vector;
import java.util.Observable;
import java.awt.event.*;

/**
 * Use this class to initialize and interact with a MIDI sequencer.
 * @author Karl Edge
 */

public class Controller extends Observable 
    implements ControllerEventListener, ActionListener 
{
    private Sequencer sequencer;
    private Sequence sequence;
    private float tempo;
    private int[] controlEvents;

    public Controller () {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            System.out.println("Sequencer open");        
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);    
        }

        try {
            sequence = new Sequence(Sequence.PPQ, 4);
            System.out.println("Sequence created");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);    
        }
    }

    public void setControlEvents (int[] events) {
        controlEvents = events;
        sequencer.addControllerEventListener(this, events);
    }

    // Called when MIDI control event is triggered
    public void controlChange (ShortMessage m) {
        setChanged();
        notifyObservers(m.getData2()); // Sends tick number to observers
    }

    public Track createTrack () {
        return sequence.createTrack();
    }

    public void setTempo (Float tempo) {
        this.tempo = tempo;
    }

    public void start () {
        try {
            sequencer.setSequence(sequence);
            //sequencer.setTickPosition(0);
            sequencer.setTempoInBPM(tempo);
            sequencer.start();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void stop () {
        try {
            sequencer.stop();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void setLoopCount (int n) {
        sequencer.setLoopCount(n);
    }

    public void actionPerformed (ActionEvent event) {
        String cmd = event.getActionCommand();
        boolean isRunning = sequencer.isRunning();
        if (cmd == "Start") {
            if (!isRunning) {
                // System.out.println(sequencer.getTempoInBPM());
                // System.out.println(sequencer.getTempoFactor());
                // System.out.println(sequencer.getTickLength());
                start();    
            }    
        } 
        else if (cmd == "Stop") {
            if (isRunning) {
                stop();
            }
        }
    }
}

