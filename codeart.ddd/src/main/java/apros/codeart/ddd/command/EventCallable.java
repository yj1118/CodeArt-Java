package apros.codeart.ddd.command;

import apros.codeart.ddd.saga.internal.EventLoader;
import apros.codeart.ddd.saga.internal.trigger.EventTrigger;
import apros.codeart.dto.DTObject;

public final class EventCallable {

	private EventCallable() {
	}

	public DTObject execute(String eventName, DTObject input) {
		var event = EventLoader.find(eventName, true);
		return EventTrigger.start(event, input);
	}

}
