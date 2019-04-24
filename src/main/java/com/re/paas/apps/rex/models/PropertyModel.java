package com.re.paas.apps.rex.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.apps.rex.calendar_schedule.CalendarMonth;
import com.re.paas.apps.rex.calendar_schedule.ScheduleCalendar;
import com.re.paas.apps.rex.classes.spec.PropertyViewRequestStatus;
import com.re.paas.apps.rex.models.tables.PropertyTable;
import com.re.paas.internal.classes.spec.PublicHolidaySpec;
import com.re.paas.internal.models.HolidayModel;
import com.re.paas.internal.models.errors.RexError;

public class PropertyModel extends BaseModel {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String path() {
		return "core/property";
	}

	public static void newPropertyViewRequest(Long userId, Long propertyId, Date viewingDate) {

		PropertyTable pe = BasePropertyModel.getProperty(propertyId);

		// Verify that the said time is not a holiday in the country of the
		// agentOrganization
		if (HolidayModel.getHoliday(pe.getCountry(), viewingDate) != null) {
			throw new PlatformException(RexError.THE_SPECIFIED_DATE_IS_A_HOLIDAY);
		}

		// get available agents, verify that at least the agents are available at the
		// said time

		// Send SMS to selected agent's phone

		// Add to activity stream

	}

	public static void isAgentAvailable(Long agent, Date viewingDate) {

	}

	public static void updatePropertyViewRequestStatus(PropertyViewRequestStatus status, String statusMessage) {

		// Add to activity stream

	}

	public static void getScheduleCalendar(Long agentOrganization) {

		ScheduleCalendar sc = new ScheduleCalendar();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Locale locale = Locale.getDefault();// here, get user's locale

		String country = "US"; // get country of property's location

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		for (int i = 0; i < 60; i++) {

			calendar.add(Calendar.DAY_OF_MONTH, 1);

			CalendarMonth month = new CalendarMonth(calendar.get(Calendar.DAY_OF_MONTH),
					calendar.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG_FORMAT, locale));

			// Should be a working day

			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
				// unavailable
			}

			// Should not be a public holiday in the property's country
			PublicHolidaySpec hs = HolidayModel.getHoliday(country, calendar.getTime());
			if (hs == null) {
				// unavailable
			}

			System.out.println(dateFormat.format(calendar.getTime()));

			int week = calendar.get(Calendar.WEEK_OF_MONTH);
			int day = calendar.get(Calendar.DAY_OF_WEEK);

			List<Long> agents = Lists.newArrayList();

		}

	}

	@Override
	public void install(InstallOptions options) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
