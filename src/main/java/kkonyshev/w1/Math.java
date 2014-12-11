package kkonyshev.w1;

import java.text.DateFormat;
import java.util.List;

import mfk.domain.Profile;
import mfk.domain.ProfileService;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
 
 
public class Math extends WebPage {
	
	@SpringBean
	private ProfileService profileService;
	
	@SpringBean
	private DateFormat dateFormat;
	
	public Math(PageParameters aParameters) {
        super(aParameters);

        add(new Label("profileCount", String.valueOf(profileService.profileCount())));
    
        Model<Profile> m = new Model<Profile>(profileService.getProfileList().get(0));
        final Form<Profile> form = new ProfileForm("profile", m);

        List<Profile> profiles = profileService.getProfileList(); 
        ChoiceRenderer<Profile> personRender = new ChoiceRenderer<Profile>("name");
        DropDownChoice<Profile> dropDownChoice = new DropDownChoice<Profile>("profileListId", new Model<Profile>(), profiles, personRender) {
			private static final long serialVersionUID = 4283427681505993382L;
			@Override
    		protected boolean wantOnSelectionChangedNotifications() {
    			return true;
    		}
        	@Override
        	protected void onSelectionChanged(Profile newSelection) {
        		super.onSelectionChanged(newSelection);
        		form.setModelObject(newSelection);
        	}
        };
        
		add(dropDownChoice);
		add(form);
    }
    
    public class ProfileForm extends Form<Profile> {
		private static final long serialVersionUID = 4044401222447029345L;

		public ProfileForm(final String id, IModel<Profile> m) {
    		super(id, m);
    		
    		TextField<String> tName = new TextField<String>("name", new ProfileNameModel(m));
    		TextField<String> tAge  = new TextField<String>("birthDate", new ProfileDateModel(m));
    		
    		add(tName);
    		add(tAge);
    	}
		
		@Override
		protected void onSubmit() {
			profileService.save(getModelObject());
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