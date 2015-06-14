import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * This class is used to group together typical MIDI events of a sound pulse.
 * @author Karl Edge
 */

public class MidiEventGroup {
    private MidiEvent noteOnEvent = null;
    private MidiEvent noteOffEvent = null;
    private MidiEvent ctrlEvent = null;
    private ShortMessage noteOnMsg = null;
    private ShortMessage noteOffMsg = null;
    private ShortMessage ctrlMsg = null;
    private final int NOTE_ON = ShortMessage.NOTE_ON;
    private final int NOTE_OFF = ShortMessage.NOTE_OFF;
    private final int CONTROL_CHANGE = ShortMessage.CONTROL_CHANGE;
    private int tick;
    private int channel;
    private int data1;
    private int data2;
    private int duration;
    private int eventID;
    private int delay = 1;
    
    public MidiEventGroup (int tick, int channel, int data1, int data2, int duration) {
        noteOnMsg = createMessage(NOTE_ON, channel, data1, data2);
        noteOnEvent = new MidiEvent(noteOnMsg, tick);
        
        noteOffMsg = createMessage(NOTE_OFF, channel, data1, data2);
        noteOffEvent = new MidiEvent(noteOffMsg, tick+duration);

        this.tick = tick;
        this.channel = channel;
        this.data1 = data1;
        this.data2 = data2;
        this.duration = duration;
    }

    public ShortMessage createMessage (int command, int channel, int data1, int data2) {
        ShortMessage s = null;
        try {
            s = new ShortMessage(command, channel, data1, data2);    
        } catch (Exception ex) {ex.printStackTrace();}
        return s;
    }

    public MidiEvent createControlEvent (int eventID) {
        ctrlMsg = createMessage(CONTROL_CHANGE, channel, eventID, tick);
        ctrlEvent = new MidiEvent(ctrlMsg, tick+delay);
        this.eventID = eventID;
        return ctrlEvent;
    }

    public MidiEvent getControlEvent () {
        return ctrlEvent;
    }

    public MidiEvent[] getEvents () { 
        return new MidiEvent[] {noteOnEvent, noteOffEvent, ctrlEvent};
    }

    public void setData1 (int data1) {
        try {
            noteOnMsg.setMessage(NOTE_ON, channel, data1, data2);
            noteOffMsg.setMessage(NOTE_OFF, channel, data1, data2);
            this.data1 = data1;
        } catch (Exception ex) {ex.printStackTrace();}
    }

    public void setTick (int tick) {
        noteOnEvent.setTick(tick);
        noteOffEvent.setTick(tick+duration);
        if (ctrlEvent != null) {
            ctrlEvent.setTick(tick);
        }
    }
}