import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;

/**
 * This class is a bridge between an array of on/off sound pulses and a track 
 * that the sequencer can actually play.
 * @author Karl Edge
 */

public class Timeline {
    private MidiEventGroup[][] groups;
    private Track track;
    private int pulseCount;
    private int tickCount;
    private int repeatCount;

    public Timeline (Track track, int pulseCount, int tickCount, int repeatCount) {
        groups = new MidiEventGroup[repeatCount][pulseCount];
        this.pulseCount = pulseCount;
        this.track = track;
        this.tickCount = tickCount;
        this.repeatCount = repeatCount;
        
        // Populate last tick so sequencer loops normally
        addGroupToTrack(createEmptyGroup((pulseCount*tickCount*repeatCount)));
    }

    private MidiEventGroup createGroup (int pulse, int channel, int pitch, 
        int velocity, int duration, int repeatN) 
    {
        int tick = calcTick(pulse, repeatN); 
        return new MidiEventGroup(tick, channel, pitch, velocity, duration);
    }

    private MidiEventGroup createEmptyGroup (int tick) {
        return new MidiEventGroup(tick, 9, 42, 0, 0);
    }

    private void addGroupToTrack (MidiEventGroup m) {
        MidiEvent[] events = m.getEvents();
        for (int i = 0; i < events.length; i++) {
            track.add(events[i]);
        }
    }

    private int calcTick (int pulse, int repeatN) {
        return (pulse * tickCount) + (repeatN * pulseCount * tickCount);
    }

    public void createAllPulses (int channel, int pitch, int velocity, int duration) {
        for (int repeatN = 0; repeatN < repeatCount; repeatN++) {
            for (int pulse = 0; pulse < pulseCount; pulse++) {
                MidiEventGroup m = createGroup(pulse, channel, pitch, velocity, 
                    duration, repeatN); 
                addGroup(repeatN, pulse, m);
            }
        }        
    }

    public void createPulse (int pulse, int channel, int pitch, int velocity, int duration) {
        for (int repeatN = 0; repeatN < repeatCount; repeatN++) {
            MidiEventGroup m = createGroup(pulse, channel, pitch, velocity, 
                duration, repeatN); 
            addGroup(repeatN, pulse, m);
        }
    }

    private void addGroup (int repeatN, int pulse, MidiEventGroup m) {
        addGroupToTrack(m);
        groups[repeatN][pulse] = m;
    }

    private void addEventToTrack (MidiEvent e) {
        track.add(e);
    }    

    public void createAllControlEvent (int eventID) {
        for (int repeatN = 0; repeatN < repeatCount; repeatN++) {
            for (int pulse = 0; pulse < pulseCount; pulse++) {
                MidiEventGroup m = groups[repeatN][pulse];  
                if (m != null) {
                    addEventToTrack(groups[repeatN][pulse].createControlEvent(eventID));
                }
            }
        }
    }

    public Track getTrack () {
        return track;
    }
}
