package schedule;

public record Time(int hourValue, Period period) {
    public Time copy() {
        return new Time(hourValue, period);
    }

    public int get24HourValue() {
        if(period.equals(Period.PM) && this.hourValue < 12) {
            return hourValue + 12;
        }
        if(period.equals(Period.AM) && this.hourValue == 12) {
            return 0;
        }
        return hourValue;
    }

    public int differenceBetween(Time other) {
        int convertedHourValue1 = this.get24HourValue();
        int convertedHourValue2 = other.get24HourValue();

        return Math.abs(convertedHourValue1 - convertedHourValue2);
    }

    public String toPrettyString() {
        return hourValue + period.name();
    }
}
