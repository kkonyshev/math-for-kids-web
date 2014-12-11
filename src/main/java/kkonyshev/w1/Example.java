package kkonyshev.w1;

import java.math.BigDecimal;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
 
 
public class Example extends WebPage {
	
    private String totalAmount;
    private Integer amountInt;
 
    @SuppressWarnings("serial")
	public Example(PageParameters aParameters) {
        super(aParameters);

        final Label totalAmountLabel = new Label("totalAmount", new PropertyModel<Integer>(this, "totalAmount"));
        
        totalAmountLabel.setOutputMarkupId(true);
        add(totalAmountLabel);
        Link<Integer> link = new AjaxFallbackLink<Integer>("amountLink", new PropertyModel<Integer>(this, "value")) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (target != null) {
                    Integer value = (Integer) getModelObject();
                    totalAmount = extractTotalAmount(value);
                    amountInt = value;
                    target.addComponent(totalAmountLabel);
                }
            }
        };
        link.add(new Label("description", "пополнить"));
        add(link);
        amountInt = 0;
        totalAmount = extractTotalAmount(amountInt);
    }
 
 
    public String getTotalAmount() {
        return totalAmount;
    }
 
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
 
    public Integer getValue() {
        return 5;
    }
 
 
    private String extractTotalAmount(Integer value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(1.008));
        return String.format("%.2f", bigDecimal);
    }

}