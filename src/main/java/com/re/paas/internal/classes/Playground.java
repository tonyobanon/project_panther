package com.re.paas.internal.classes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.ibm.icu.util.Holiday;
import com.ibm.icu.util.ULocale;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.LogPipeline;
import com.re.paas.api.sentences.Preposition;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.apps.rex.calendar_schedule.CalendarMonth;
import com.re.paas.apps.rex.calendar_schedule.ScheduleCalendar;
import com.re.paas.internal.classes.spec.ClientSignatureType;
import com.re.paas.internal.classes.spec.PublicHolidaySpec;
import com.re.paas.internal.entites.payments.InvoicePaymentHistoryEntity;
import com.re.paas.internal.logging.DefaultLogger;
import com.re.paas.internal.models.BaseAgentModel;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.TaskModel;
import com.re.paas.internal.networking.NIC;
import com.re.paas.internal.models.CurrencyModel;
import com.re.paas.internal.models.HolidayModel;
import com.re.paas.internal.models.LocaleModel;

public class Playground {

	public static void main(String[] args) throws UnknownHostException {
		
			//byte[] b = InetAddress.getByName("1.1.1.1").getAddress();
		
//		byte[] bytes = InetAddress.getByName("127.255.74.202").getAddress();
//
//		for (byte b : bytes) {
//		   System.out.format("0x%x ", b);
//		}
		
		byte[] b = new byte[] {0x0, 0xb, 0x0, 0x0};
		for(byte bb : InetAddress.getByName("0.11.0.0").getAddress()){
			System.out.format("0x%x ", bb);
		}
			
		//0x7f 0xff 0x4a 0xca
		
//		HolidayModel.getHolidays("NG").forEach(e -> {
//			System.out.println("\n____________");
//			System.out.println(e.getName());
//			System.out.println(e.getDate());
//			System.out.println(e.isPublic());
//		});
		
		
		
//		Calendar now = Calendar.getInstance();
//		
//		Integer fromDay = now.get(Calendar.DAY_OF_MONTH);
//		String fromMonth = now.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("en-US"));
//		Integer fromYear = now.get(Calendar.YEAR);
//		
//		//Utils
//		
//		CronModel.getNextExecutionTime(CronInterval.MONTHLY);
//		
//		String from = fromMonth + ClientResources.HtmlCharacterEntities.SPACE + fromDay.toString()+ ClientResources.HtmlCharacterEntities.COMMA + ClientResources.HtmlCharacterEntities.SPACE + fromYear;
//		
		
		
		//Locale l = Locale.forLanguageTag("en-GB");
		//ULocale l2 = ULocale.forLanguageTag("en-NG");
		
		
		//for(Holiday h : Holiday.getHolidays(l2)) {
		//	System.out.println(h.getDisplayName());
		//}
		
		//		
//		Pattern p = ListingFilter.queryFilterKey;
//		Matcher m = p.matcher("obinna>=<<< ");
//		
//		while(m.find()) {
//			System.out.println(m.group());
//		}
//		
		
//		
//		System.out.println(CurrencyModel.getCurrencyRate("USD", "NGN"));
//		
//		
		
//		Pattern addressPattern = Pattern.compile("\\A[0-9]+[ ]*(\\Q,\\E)*[ ]*");
//		Matcher m = addressPattern.matcher("34, address street, roju avenue off Roju");
//		
//		while(m.find()) {
//			System.out.println(m.group());
//		}
		
		
//		String uri = "http://facebook.com/anthony_a/f";
//		
//		System.out.println(BaseUserModel.getSocialHandleId(uri));
//		
//		
		
		
//		QueryFilter f = new QueryFilter();
//		
//		Object h = f;
//		
//		System.out.println(h.getClass().getName());
//		
//		
//		TimeZone.getTimeZone("PST").getRawOffset();
		
//		
		
		
//		String o = "WITH, AT, FROM, INTO, DURING, INCLUDING, UNTIL, AGAINST, AMONG, THROUGHOUT, DESPITE, TOWARDS, UPON, CONCERNING, OF, TO, IN, FOR, ON, BY, ABOUT, " + 
//				"LIKE, THROUGH, OVER, BEFORE, BETWEEN, AFTER, SINCE, WITHOUT, UNDER, WITHIN, ALONG, FOLLOWING, ACCROSS, BEHIND, BEYOND, PLUS, EXCEPT, BUT, UP, " + 
//				"OUT, AROUND, DOWN, OFF, ABOVE, NEAR";
//		
//		
//		
//		for(String i : o.split(", ")) {
//			System.out.println(i.toLowerCase() + "=" + i.toLowerCase().replace("_", " "));
//		}
//		
//		
		//System.out.println(Dates.toString(new Date()));
		
//		
		
		
//		System.out.println(Boolean.parseBoolean("undefined"));
		
//		Map<Preposition, String> v = new HashMap<>();
//		
//		v.put(Preposition.ABOUT, "1");
//		
//		
//		System.out.println(v.get(Preposition.ABOUT));
//		
		
//		Locale l = Locale.forLanguageTag("en-US");
//	
//		System.out.println(l.getDisplayName());
//		System.out.println(l.getDisplayScript());
//		System.out.println(l.getISO3Language() + "-" + l.getISO3Country());
//		
		
//		int duration = 5;
//		
//		for (int i = 100; i < ((duration * 100) + 1 ); i += 100) {
//			System.out.println(i);
//		}
		
		
		
		
//		permute(new int[]{1, 2, 3}).forEach(l1 -> {
//			l1.forEach(i -> {
//				System.out.print(i + " ");
//			});
//			System.out.println("");
//		});
		
		
		
		
//		int pageSize = 60;
//		
//		Integer keysSize = 121;
//		
//		Integer pageCount = null;
//		
//		if (keysSize <= pageSize) {
//			pageCount = 1;
//		} else {
//			pageCount = keysSize / pageSize;
//			
//			if(keysSize % pageSize > 0) {
//				pageCount += 1;
//			}
//		}
//		
//		
//		System.out.println(pageCount);

	}

	public static ArrayList<ArrayList<Integer>> permute(int[] num) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

		// start from an empty list
		result.add(new ArrayList<Integer>());

		for (int i = 0; i < num.length; i++) {
			// list of list in current iteration of the array num
			ArrayList<ArrayList<Integer>> current = new ArrayList<ArrayList<Integer>>();

			for (ArrayList<Integer> l : result) {
				// # of locations to insert is largest index + 1
				for (int j = 0; j < l.size() + 1; j++) {
					// + add num[i] to different locations
					l.add(j, num[i]);

					ArrayList<Integer> temp = new ArrayList<Integer>(l);
					current.add(temp);

					// System.out.println(temp);

					// - remove num[i] add
					l.remove(j);
				}
			}

			result = new ArrayList<ArrayList<Integer>>(current);
		}

		return result;
	}

}
