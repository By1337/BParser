package org.by1337.bparser.event;

public interface VelocityUpdate {
    void on(Data data);
    class Data {
       final int id;
       final double x;
       final double y;
       final double z;

        public Data(int id, double x, double y, double z) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int id() {
            return id;
        }

        public double x() {
            return x;
        }

        public double y() {
            return y;
        }

        public double z() {
            return z;
        }
    }
}
