package kkonyshev.w1;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collections;
import java.util.List;

import mfk.domain.NumberStat;
import mfk.domain.Profile;
import mfk.domain.ProfileService;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

public class Math extends WebPage {

	@SpringBean
	private ProfileService profileService;

	@SpringBean
	private DateFormat dateFormat;

	private class CounterModel implements IModel<Counter> {
		private static final long serialVersionUID = 4000409785547641611L;
		private Counter counter;

		public CounterModel(Counter counter) {
			this.counter = counter;
		}

		@Override
		public void detach() {

		}

		@Override
		public Counter getObject() {
			return counter;
		}

		@Override
		public void setObject(Counter object) {
			this.counter = object;
		}
	}

	public class Counter implements Serializable {
		private static final long serialVersionUID = -8376221702686314362L;
		private Integer count = 0;

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}

	public Math(PageParameters aParameters) {
		super(aParameters);

		final Profile p = profileService.getProfileList().get(0);
		Model<Profile> m = new Model<Profile>(p);
		final Form<Profile> form = new ProfileForm("profile", m);

		List<Profile> profiles = profileService.getProfileList();

		final NumberListView numberListView = new NumberListView("listview",
				form.getModel());
		final ProfileNumberModel profileNumberModel = new ProfileNumberModel(m);

		ChoiceRenderer<Profile> personRender = new ChoiceRenderer<Profile>(
				"name");
		DropDownChoice<Profile> dropDownChoice = new DropDownChoice<Profile>(
				"profileListId", new Model<Profile>(), profiles, personRender) {
			private static final long serialVersionUID = 4283427681505993382L;

			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

			@Override
			protected void onSelectionChanged(Profile newSelection) {
				super.onSelectionChanged(newSelection);
				form.setModelObject(newSelection);
				numberListView.setModel(profileNumberModel);
			}
		};
		add(dropDownChoice);
		add(form);
		add(numberListView);

		final Counter counter = new Counter();
		final CounterModel counterModel = new CounterModel(counter);
		final Label countLabel = new Label("profileCount",
				new PropertyModel<Counter>(counterModel, "count"));
		AbstractAjaxTimerBehavior ajaxSelfUpdatingTimerBehavior = new AbstractAjaxTimerBehavior(
				Duration.seconds(1)) {
			private static final long serialVersionUID = -337506275608091460L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				int count = counter.getCount() + 1;
				System.out.println("next id is: " + count);
				counter.setCount(count);
				countLabel.renderComponent();
			}
		};
		countLabel.add(ajaxSelfUpdatingTimerBehavior);
		add(countLabel);
	}

	private void addRandomNumber(final Profile p) {
		List<Integer> batch = profileService.getNextBatchToLearn(p);
		for (Integer number : batch) {
			profileService.increaseNumberStat(p, number);
		}
	}

	public class NumberListView extends ListView<NumberStat> {
		private static final long serialVersionUID = 4043401222447029345L;

		public NumberListView(final String id, IModel<Profile> m) {
			super(id, profileService.getNumberStat(m.getObject()));
		}

		@Override
		protected void populateItem(ListItem<NumberStat> item) {
			item.add(new Label("number", String.valueOf(item.getModelObject()
					.getNumber())));
			item.add(new Label("count", String.valueOf(item.getModelObject()
					.getCount())));
		}
	}

	public class ProfileForm extends Form<Profile> {
		private static final long serialVersionUID = 4044401222447029345L;

		public ProfileForm(final String id, IModel<Profile> m) {
			super(id, m);

			TextField<String> tName = new TextField<String>("name",
					new ProfileNameModel(m));
			TextField<String> tAge = new TextField<String>("birthDate",
					new ProfileDateModel(m));

			add(tName);
			add(tAge);
		}

		@Override
		protected void onSubmit() {
			Profile modelObject = getModelObject();
			addRandomNumber(modelObject);
			profileService.save(modelObject);
		}
	}

	private class ProfileNumberModel implements IModel<List<NumberStat>> {
		private static final long serialVersionUID = 553096728821564406L;
		private IModel<Profile> store;

		public ProfileNumberModel(IModel<Profile> store) {
			this.store = store;
		}

		public void detach() {
		}

		public List<NumberStat> getObject() {
			List<NumberStat> numberCount = profileService.getNumberStat(store
					.getObject());
			Collections.sort(numberCount);
			return numberCount;
		}

		@Override
		public void setObject(List<NumberStat> object) {
			profileService.setNumberStat(object);
		}
	}

	private class ProfileNameModel implements IModel<String> {
		private static final long serialVersionUID = 553096728821564406L;
		private IModel<Profile> store;

		public ProfileNameModel(IModel<Profile> store) {
			this.store = store;
		}

		public void detach() {
		}

		public String getObject() {
			return store.getObject().getName();
		}

		public void setObject(String object) {
			store.getObject().setName(object);
		}
	}

	private class ProfileDateModel implements IModel<String> {
		private static final long serialVersionUID = 553096728821564406L;
		private IModel<Profile> store;

		public ProfileDateModel(IModel<Profile> store) {
			this.store = store;
		}

		public void detach() {
		}

		public String getObject() {
			return dateFormat.format(store.getObject().getBirthDate());
		}

		public void setObject(String object) {
			try {
				store.getObject().setBirthDate(dateFormat.parse(object));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}
}