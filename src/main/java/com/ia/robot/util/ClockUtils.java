package com.ia.robot.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Utilitaire temps.
 *
 * Objectif V0:
 * - centraliser l'accès à l'horloge
 * - faciliter tests + future injection d’un Clock custom
 */
public final class ClockUtils {

    private static volatile Clock clock = Clock.systemUTC();

    private ClockUtils() {
    }

    public static Instant now() {
        return Instant.now(clock);
    }

    public static ZoneId zone() {
        return clock.getZone();
    }

    /**
     * Permet d'injecter une horloge custom (tests).
     */
    public static void use(Clock newClock) {
        if (newClock != null) {
            clock = newClock;
        }
    }

    /**
     * Restore horloge par défaut.
     */
    public static void reset() {
        clock = Clock.systemUTC();
    }
}
