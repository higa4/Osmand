package net.osmand.router;

import java.util.LinkedHashMap;
import java.util.Map;

import net.osmand.binary.BinaryMapDataObject;
import net.osmand.binary.BinaryMapIndexReader.TagValuePair;
import net.osmand.osm.MapRenderingTypes;
import net.osmand.router.BinaryRoutePlanner.RouteSegment;

public class CarRouter extends VehicleRouter {
	// no distinguish for speed in city/outside city (for now)
	private Map<String, Double> autoNotDefinedValues = new LinkedHashMap<String, Double>();
	private Map<String, Double> autoPriorityValues = new LinkedHashMap<String, Double>();
	{
		autoNotDefinedValues.put("motorway", 110d);
		autoNotDefinedValues.put("motorway_link", 80d);
		autoNotDefinedValues.put("trunk", 100d);
		autoNotDefinedValues.put("trunk_link", 80d);
		autoNotDefinedValues.put("primary", 65d);
		autoNotDefinedValues.put("primary_link", 45d);
		autoNotDefinedValues.put("secondary", 50d);
		autoNotDefinedValues.put("secondary_link", 40d);
		autoNotDefinedValues.put("tertiary", 35d);
		autoNotDefinedValues.put("tertiary_link", 30d);
		autoNotDefinedValues.put("residential", 30d);
		autoNotDefinedValues.put("road", 30d);
		autoNotDefinedValues.put("service", 20d);
		autoNotDefinedValues.put("unclassified", 20d);
		autoNotDefinedValues.put("track", 20d);
		autoNotDefinedValues.put("path", 20d);
		autoNotDefinedValues.put("living_street", 20d);

		autoPriorityValues.put("motorway", 1.5);
		autoPriorityValues.put("motorway_link", 1.0);
		autoPriorityValues.put("trunk", 1.5);
		autoPriorityValues.put("trunk_link", 1d);
		autoPriorityValues.put("primary", 1.3d);
		autoPriorityValues.put("primary_link", 1d);
		autoPriorityValues.put("secondary", 1.0d);
		autoPriorityValues.put("secondary_link", 1.0d);
		autoPriorityValues.put("tertiary", 1.0d);
		autoPriorityValues.put("tertiary_link", 1.0d);
		autoPriorityValues.put("residential", 0.8d);
		autoPriorityValues.put("service", 0.6d);
		autoPriorityValues.put("unclassified", 0.4d);
		autoPriorityValues.put("road", 0.4d);
		autoPriorityValues.put("track", 0.1d);
		autoPriorityValues.put("path", 0.1d);
		autoPriorityValues.put("living_street", 0.5d);
	}

	public boolean acceptLine(TagValuePair pair) {
		if (pair.tag.equals("highway")) {
			return autoNotDefinedValues.containsKey(pair.value);
		}
		return false;
	}

	public boolean acceptPoint(TagValuePair pair) {
		if (pair.tag.equals("traffic_calming")) {
			return true;
		} else if (pair.tag.equals("highway") && pair.value.equals("traffic_signals")) {
			return true;
		} else if (pair.tag.equals("highway") && pair.value.equals("speed_camera")) {
			return true;
		} else if (pair.tag.equals("railway") && pair.value.equals("crossing")) {
			return true;
		} else if (pair.tag.equals("railway") && pair.value.equals("level_crossing")) {
			return true;
		}
		return false;
	}

	public boolean isOneWay(int highwayAttributes) {
		return MapRenderingTypes.isOneWayWay(highwayAttributes) || MapRenderingTypes.isRoundabout(highwayAttributes);
	}

	/**
	 * return delay in seconds
	 */
	public double defineObstacle(BinaryMapDataObject road, int point) {
		if ((road.getTypes()[0] & 3) == MapRenderingTypes.POINT_TYPE) {
			// possibly not only first type needed ?
			TagValuePair pair = road.getTagValue(0);
			if (pair != null) {
				if (pair.tag.equals("highway") && pair.value.equals("traffic_signals")) {
					return 20;
				} else if (pair.tag.equals("railway") && pair.value.equals("crossing")) {
					return 25;
				} else if (pair.tag.equals("railway") && pair.value.equals("level_crossing")) {
					return 25;
				}
			}
		}
		return 0;
	}

	/**
	 * return speed in m/s
	 */
	public double defineSpeed(BinaryMapDataObject road) {
		TagValuePair pair = road.getTagValue(0);
		double speed = MapRenderingTypes.getMaxSpeedIfDefined(road.getHighwayAttributes()) / 3.6d;
		boolean highway = "highway".equals(pair.tag);
		double priority = highway && autoPriorityValues.containsKey(pair.value) ? autoPriorityValues.get(pair.value) : 1d;
		if (speed == 0 && highway) {
			Double value = autoNotDefinedValues.get(pair.value);
			if (value == null) {
				value = 50d;
			}
			speed = value / 3.6d;
		}
		return speed * priority;
	}

	/**
	 * Used for A* routing to calculate g(x)
	 * 
	 * @return minimal speed at road
	 */
	public double getMinDefaultSpeed() {
		return 9;
	}

	/**
	 * Used for A* routing to predict h(x) : it should be < (!) any g(x)
	 * 
	 * @return maximum speed to calculate shortest distance
	 */
	public double getMaxDefaultSpeed() {
		return 30;
	}

	public double calculateTurnTime(RouteSegment segment, RouteSegment next, int segmentEnd) {
		boolean end = (segmentEnd == segment.road.getPointsLength() - 1 || segmentEnd == 0);
		boolean start = next.segmentStart == 0;
		if (end) {
			if(!start){
				return 15;
			}
			return 0;
		} else {
			return 25;
		}
	}

}