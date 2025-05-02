package com.vladosik0.schedulerapp.model.validators

import com.vladosik0.schedulerapp.model.enums.TimelineEvents

// --- Check picked filters for Free Slots or Events ---
fun areOnlyTasksPicked(selectedTimelineEvents: Set<TimelineEvents>): Boolean {
    return TimelineEvents.TASKS in selectedTimelineEvents && TimelineEvents.FREE_SLOTS !in selectedTimelineEvents
}
