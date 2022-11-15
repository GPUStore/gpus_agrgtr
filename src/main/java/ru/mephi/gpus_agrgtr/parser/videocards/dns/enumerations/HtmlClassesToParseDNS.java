package ru.mephi.gpus_agrgtr.parser.videocards.dns.enumerations;

public enum HtmlClassesToParseDNS {
    SPECIFICATION_CLASS("div.product-characteristics"),
    PARAM_CLASS("div.product-characteristics__spec"),
    PARAM_NAME_CLASS("div.product-characteristics__spec-title"),
    PARAM_VALUE_CLASS("div.product-characteristics__spec-value"),
    CHARACTERISTIC_GROUP_CLASS("div.product-characteristics__group"),
    CHARACTERISTIC_NAME_CLASS("div.product-characteristics__group-title"),
    PATH_FOR_COUNT_PAGES("a.pagination-widget__page-link"),
    PATH_FOR_COSTS("div.product-buy.product-buy_one-line.catalog-product__buy"),
    SUB_PATH_FOR_COSTS("div.product-buy__price"),
    PATH_FOR_LINKS("a.catalog-product__name.ui-link.ui-link_black"),
    PAGINATION_ELEMENT("li.pagination-widget__page"),
    PAGINATION_ATTRIBUTE("data-page-number");

    private final String className;

    HtmlClassesToParseDNS(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
