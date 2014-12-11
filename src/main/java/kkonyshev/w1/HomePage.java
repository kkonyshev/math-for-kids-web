package kkonyshev.w1;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;

/**
 * Homepage
 */
public class HomePage extends WebPage {

	private static final long serialVersionUID = 1L;

	// TODO Add any page properties or variables here

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    @SuppressWarnings("serial")
	public HomePage(final PageParameters parameters) {

        // Add the simplest type of label
        add(new Label("message", "If you see this message wicket is properly configured and running"));

        // TODO Add your page's components here
		Model<Integer> model = new Model<Integer>() {
			private int counter = 0;

            public Integer getObject() {
                return counter++;
            }
        };
        add(new Link<Integer>("link") {
            public void onClick() {
                // do nothing.
            }
        });
        add(new Label("counter", model));
    }
}
