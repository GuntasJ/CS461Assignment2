package genetics;

import schedule.Activity;
import schedule.Room;
import schedule.ScheduleConstants;
import schedule.Time;

import java.util.Random;

//This is the genetic representation of an activity
public class Gene {
    private final Activity activity;
    private Room room;
    private Time time;
    private String facilitator;

    public Gene(Activity activity, Room room, Time time, String facilitator) {
        this.activity = activity;
        this.room = room;
        this.time = time;
        this.facilitator = facilitator;
    }

    Gene copyGene() {
        return new Gene(activity.copy(), room.copy(), time.copy(), facilitator);
    }

    void mutateFacilitator() {
        Random random = new Random();
        String newFacilitator;
        do {
            newFacilitator = ScheduleConstants.FACILITATORS.get(
                    random.nextInt(ScheduleConstants.NUMBER_OF_FACILITATORS)
            );
        } while (newFacilitator.equals(facilitator));
        facilitator = newFacilitator;
    }

    void mutateTime() {
        Random random = new Random();
        Time newTime;
        do {
            newTime = ScheduleConstants.TIMES.get(
                    random.nextInt(ScheduleConstants.NUMBER_OF_TIMES)
            );
        } while(newTime.equals(time));
        time = newTime;
    }

    void mutateRoom() {
        Random random = new Random();
        Room newRoom;
        do {
            newRoom = ScheduleConstants.ROOMS.get(
                    random.nextInt( ScheduleConstants.NUMBER_OF_ROOMS)
            );
        } while (newRoom.equals(room));
        room = newRoom;
    }

    public Activity getActivity() {
        return activity;
    }

    public Room getRoom() {
        return room;
    }

    public Time getTime() {
        return time;
    }

    public String getFacilitator() {
        return facilitator;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "activity=" + activity +
                ", room=" + room +
                ", time=" + time +
                ", facilitator='" + facilitator + '\'' +
                '}';
    }
}
