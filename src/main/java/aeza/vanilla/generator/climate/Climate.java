package aeza.vanilla.generator.climate;

public final class Climate {

    public record ParameterPoint(
            float temperature,
            float humidity,
            float continentalness,
            float erosion,
            float depth,
            float weirdness,
            float offset
    ) {
        public float fitness(float temp, float hum, float cont, float eros, float dep, float weird) {
            float dt = this.temperature - temp;
            float dh = this.humidity - hum;
            float dc = this.continentalness - cont;
            float de = this.erosion - eros;
            float dd = this.depth - dep;
            float dw = this.weirdness - weird;
            return dt * dt + dh * dh + dc * dc + de * de + dd * dd + dw * dw + this.offset;
        }
    }

    public record BiomeEntry(ParameterPoint point, int biomeId) {}
}
