package com.re.paas.integrated.models;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.sentences.Sentence;
import com.re.paas.api.sentences.SubjectEntity;
import com.re.paas.api.utils.Dates;
import com.re.paas.integrated.tables.defs.base.ActivityStreamTable;
import com.re.paas.integrated.tables.spec.base.ActivityStreamTableSpec;
import com.re.paas.internal.classes.ActivitityStreamTimeline;
import com.re.paas.internal.classes.BackendObjectMarshaller;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.sentences.DefaultSentenceStringifier;

@BlockerTodo("Create sentence serializers and deserializer, since they will be stored as Json very soon")

@BlockerTodo("Maintaining activity streams are extremely expensive, and as such, find a workaround")

@BlockerTodo("Create a configuration, that allows admins to set the default page size when\n"
		+ "retrieving activity streams on the client")

public class ActivityStreamModel extends BaseModel {

	@Override
	public String path() {
		return "core/activity_stream";
	}

	@Override
	public void preInstall() {
		enable();
		ConfigModel.putString(ConfigKeys.DEFAULT_ACTIVITY_STREAM_TIMELINE, ActivitityStreamTimeline.DAILY.getValue().toString());
	}

	public static ActivitityStreamTimeline getActivityTimeline() {
		return ActivitityStreamTimeline
				.from(Integer.parseInt(ConfigModel.get(ConfigKeys.DEFAULT_ACTIVITY_STREAM_TIMELINE)));
	}

	public static void setActivityTimeline(ActivitityStreamTimeline timeline) {
		ConfigModel.putString(ConfigKeys.DEFAULT_ACTIVITY_STREAM_TIMELINE, timeline.getValue().toString());
	}

	public static void disable() {
		ConfigModel.putString(ConfigKeys.ACTIVITY_STREAM_ENABLED, BackendObjectMarshaller.marshal(false));
	}

	public static void enable() {
		ConfigModel.putString(ConfigKeys.ACTIVITY_STREAM_ENABLED, BackendObjectMarshaller.marshal(true));
	}

	public static Boolean isEnabled() {
		return BackendObjectMarshaller.unmarshalBool(ConfigModel.get(ConfigKeys.ACTIVITY_STREAM_ENABLED));
	}

	@Todo("Add support for indexing multiple persons in a single ActivityStreamEntity")
	@BlockerTodo("This method does not update the ActivityStreamList in cache. Do so")
	protected static void newActivity(Sentence activity) {

		if (!isEnabled()) {
			// Logger.debug("Could not save activity because activity streams are currently
			// disabled");
			return;
		}

		// Try to stringify activity
		// activity.toString();

		Long subject = null;
		if (activity.getSubject() instanceof SubjectEntity) {
			SubjectEntity _subject = (SubjectEntity) activity.getSubject();
			subject = (Long.parseLong(_subject.getIdentifiers().get(0).toString()));
		}

		Long person = null;

		for (Object p : activity.getPrepositions().values()) {
			if (p instanceof SubjectEntity) {
				SubjectEntity _person = (SubjectEntity) p;
				person = (Long.parseLong(_person.getIdentifiers().get(0).toString()));
				break;
			}
		}

		Item item = new Item()
				.withString(ActivityStreamTableSpec.ACTIVITY, activity.asString(new DefaultSentenceStringifier()))
				.withNumber(ActivityStreamTableSpec.SUBJECT, subject)
				.withString(ActivityStreamTableSpec.SUBJECT_IMAGE,
						subject != null ? BaseUserModel.getAvatar(subject) : null)
				.withString(ActivityStreamTableSpec.PERSON_IMAGE,
						person != null ? BaseUserModel.getAvatar(person) : null)
				.with(ActivityStreamTableSpec.LIKES, 0).with(ActivityStreamTableSpec.DATE, Dates.now());

		Table t = Database.get().getTable(ActivityStreamTable.class);
		t.putItem(PutItemSpec.forItem(item));
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
	public void unInstall() {
		// TODO Auto-generated method stub

	}
}
