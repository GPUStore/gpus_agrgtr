package ru.mephi.gpus_agrgtr.parser.videocards.dns.enumerations;

public enum HtmlClasses {
    SPECIFICATION("div.product-characteristics"),
    SPECIFICATION_PARAM("div.product-characteristics__spec"),
    PARAM_NAME("div.product-characteristics__spec-title"),
    PARAM_VALUE("div.product-characteristics__spec-value"),
    CHARACTERISTIC_GROUP("div.product-characteristics__group"),
    CHARACTERISTIC_NAME("div.product-characteristics__group-title"),
    PAGINATION("a.pagination-widget__page-link"),
    COST("div.product-buy.product-buy_one-line.catalog-product__buy"),
    SUB_FOR_COSTS("div.product-buy__price"),
    LINK("a.catalog-product__name.ui-link.ui-link_black"),
    PAGINATION_ELEMENT("li.pagination-widget__page"),
    PAGINATION_ATTRIBUTE("data-page-number");

    private final String name;

    HtmlClasses(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
