package main.java.wg_gesucht;

import org.jsoup.nodes.Document;

public class DocBundle {
    private Document offer_doc;
    private Document contact_form;
    private int city_id;

    public DocBundle(
        Document offer_doc,
        Document contact_form,
        int city_id
    ) {
        this.offer_doc = offer_doc;
        this.contact_form = contact_form;
        this.city_id = city_id;
    }

    public int getCityID() {
        return this.city_id;
    }

    public Document getOfferDoc() {
        return this.offer_doc;
    }

    public Document getContactForm() {
        return this.contact_form;
    }
}
