import javax.sound.midi.*;
import java.util.Vector;
import java.util.Observable;
import java.awt.event.*;

/**
 * Use this class to initialize and interact with a MIDI sequencer.
 * @author Karl Edge
 */

public class Controller extends Observable implements ControllerEventListener, ActionListener {
    private Sequencer sequencer;
    private Sequence sequence;
    private Vector timelines;
    private float tempo;

    public Controller () {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            System.out.println("Sequencer open");        
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);    
        }

        // Pause for sequencer to start up.
        try {
            Thread.sleep(3000);              //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        try {
            sequence = new Sequence(Sequence.PPQ, 4);
            System.out.println("Sequence created");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);    
        }

        timelines = new Vector();
        
        // MIDI events with these codes will trigger controlChange()
        int[] events = {127};
        sequencer.addControllerEventListener(this, events);
    }

    public void controlChange (ShortMessage m) {
        setChanged();
        notifyObservers(m.getData2()); // Sends tick number to observers
    }

    public Timeline addTimeline (int pulseCount, int tickCount, int repeatCount) {
        Track track = sequence.createTrack();
        Timeline timeline = new Timeline(track, pulseCount, tickCount, repeatCount);
        timelines.addElement(timeline);
        return timeline;
    }
    
    public void deleteTimeline (Timeline t) {
        sequence.deleteTrack(t.getTrack());
        timelines.remove(t);
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

