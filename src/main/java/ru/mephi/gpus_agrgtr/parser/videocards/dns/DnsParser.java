package ru.mephi.gpus_agrgtr.parser.videocards.dns;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mephi.gpus_agrgtr.entity.*;
import ru.mephi.gpus_agrgtr.parser.Parser;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.enumerations.HtmlClassesToParseDNS;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.response.FullDTO;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.response.ParamDTO;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.response.SpecificationsDTO;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.mephi.gpus_agrgtr.utils.StringUtils.getByPattern;

@Slf4j
@Service
public class DnsParser extends Parser {
    private final String storeUrl;
    private final WebDriver webDriver;
    private static final Integer DEFAULT_WAIT = 3000;
    private static final Integer START_WAIT_TIME_AS_MILLIS_ELEMENT = 500;
    private static final Integer EXTRA_TIME_AS_SECONDS = 2;
    private static final String NUMBER = "\\d+";
    private static final String NAME = "[^а-яА-Я]";
    private static final String NAME_BRACKETS = "[\\[\\]]";

    public DnsParser(@Value("${dns.url.base}") String url,
                     @Value("${dns.url.list}") String storeUrl,
                     @Value("${dns.name}") String storeName,
                     ObjectMapper mapper,
                     WebDriver webDriver) {
        super(url, storeName, mapper);
        this.storeUrl = storeUrl;
        this.webDriver = webDriver;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            Document page = getPageWithDefaultWait(storeUrl);
            int countPages = getCountPages(page);

            for (int numberOfPage = 1; numberOfPage <= countPages; numberOfPage++) {
                List<Double> costs = getCosts(page);
                log.info("Got pages :{}", costs.size());
                List<String> links = getLinks(page);
                log.info("Got links :{}", links.size());
                List<String> names = getNames(page);
                log.info("Got names :{}", names.size());
                isNotEqualsSizeThrowException(costs, links, names);
                for (int i = 0; i < links.size(); i++) {
                    try {
                        page = getPageWithWaitingSomeElements(links.get(i),
                                List.of(HtmlClassesToParseDNS.SPECIFICATION_CLASS.getClassName()));
                        products.add(getProduct(page, costs.get(i), links.get(i), names.get(i)));
                        log.info("Product with link :{} was handled", links.get(i));
                    } catch (RuntimeException e) {
                        log.info("Product with link [" + links.get(i) + "] was not created." + e.getMessage());
                    }
                }
                if(numberOfPage <= countPages){
                    page = getPageWithDefaultWait(String.format(storeUrl, numberOfPage));
                }
            }
        } finally {
            webDriver.close();
            webDriver.quit();
        }
        return products;
    }

    private List<Double> getCosts(Document page) {
        return page.select(HtmlClassesToParseDNS.PATH_FOR_COSTS.getClassName())
                .select(HtmlClassesToParseDNS.SUB_PATH_FOR_COSTS.getClassName())
                .stream()
                .filter(Objects::nonNull)
                .map(Element::text)
                .map(textWithCost -> getByPattern(NUMBER, textWithCost))
                .map(Double::parseDouble)
                .toList();
    }

    private int getCountPages(Document page) {
        String className = Optional
                .ofNullable(page.select(HtmlClassesToParseDNS.PAGINATION_ELEMENT.getClassName()).last())
                .orElseThrow(() -> new RuntimeException("Unable to load pagination element"))
                .attr(HtmlClassesToParseDNS.PAGINATION_ATTRIBUTE.getClassName());
        return Integer.parseInt(className);
    }

    private List<String> getNames(Document page) {
        return page.select(HtmlClassesToParseDNS.PATH_FOR_LINKS.getClassName()).stream()
                .filter(Objects::nonNull)
                .map(Element::text)
                .map(title -> getByPattern(NAME, title))
                .map(name -> name.trim().toUpperCase())
                .map(DnsParser::getReplacedName)
                .toList();
    }

    public static String getReplacedName(String strn) {
        Pattern pattern = Pattern.compile(NAME_BRACKETS);
        Matcher matcher = pattern.matcher(strn);
        int count = 0;
        while (matcher.find()) {
            if (count >= 3) {
                break;
            }
            strn = switch (++count) {
                case 1 -> {
                    char[] chars = strn.toCharArray();
                    chars[matcher.start()] = '(';
                    yield String.valueOf(chars);
                }
                case 2 -> {
                    char[] chars = strn.toCharArray();
                    chars[matcher.start()] = ')';
                    yield String.valueOf(chars);
                }
                case 3 -> strn.substring(0, matcher.start()).trim();
                default -> strn;
            };
        }
        return strn;
    }

    private List<String> getLinks(Document page) {
        return page.select(HtmlClassesToParseDNS.PATH_FOR_LINKS.getClassName()).stream()
                .filter(Objects::nonNull)
                .map(element -> element.attr("href"))
                .map(href -> url + href)
                .toList();
    }

    private void isNotEqualsSizeThrowException(List<Double> costs, List<String> links, List<String> names) {
        int size = costs.size();
        if (size != links.size() || size != names.size()) {
            throw new RuntimeException("Website data integrity error.");
        }
    }

    private Document getPageWithWaitingSomeElements(String pageUrl, List<String> classElements) {
        webDriver.get(pageUrl);
        checkDownloaded(classElements);
        String stringPage = webDriver.getPageSource();
        if (stringPage == null) {
            throw new RuntimeException("The page was not retrieved, probably the wrong url");
        }
        return Jsoup.parse(stringPage);
    }

    private Document getPageWithDefaultWait(String pageUrl) {
        webDriver.get(pageUrl);
        try {
            Thread.sleep(DEFAULT_WAIT);
        } catch (InterruptedException e) {
            throw new RuntimeException("Problem with thread ", e);
        }
        String stringPage = webDriver.getPageSource();
        if (stringPage == null) {
            throw new RuntimeException("The page was not retrieved, probably the wrong url");
        }
        return Jsoup.parse(stringPage);
    }

    public void checkDownloaded(List<String> classElements) {
        boolean checked = delayControl(classElements);
        if (!checked) {
            throw new RuntimeException("Unable to load page");
        }
    }

    private boolean delayControl(List<String> classElements) {
        Duration current = Duration.ofMillis(START_WAIT_TIME_AS_MILLIS_ELEMENT);
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, current);
        AtomicReference<Boolean> curFlag = new AtomicReference<>(false);
        Boolean generalFlag = true;
        ArrayList<String> list = new ArrayList<>();
        int count = 0;
        do {
            WebDriverWait finalWebDriverWait = webDriverWait;
            classElements.parallelStream().forEach(classElement -> {
                curFlag.set(finalWebDriverWait.until(ExpectedConditions.invisibilityOfElementLocated
                        (By.className(classElement))));
                if (curFlag.get()) {
                    list.add(classElement);
                    curFlag.set(false);
                }
            });
            if (!list.isEmpty()) {
                for (String s : list) {
                    if (generalFlag) {
                        generalFlag = webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated
                                (By.className(s)));
                    } else {
                        webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated
                                (By.className(s)));
                    }
                }
            }
            webDriverWait = new WebDriverWait(webDriver, current.plus(Duration.ofSeconds(EXTRA_TIME_AS_SECONDS)));
            count++;
        } while (!generalFlag || count != 3);
        return generalFlag;
    }

    private Product getProduct(Document page, Double cost, String link, String name) {
        SpecificationsDTO specificationsDTO = requestData(page);
        Store store = new Store()
                .setName(storeName)
                .setUrl(link)
                .setCost(cost);
        return new Product()
                .setType(Type.VIDEOCARD)
                .setName(name)
                .setStores(List.of(store))
                .setParameters(toParameters(specificationsDTO.getFull()));
    }

    private SpecificationsDTO requestData(Document page) {
        SpecificationsDTO specificationsDTO = new SpecificationsDTO();
        List<FullDTO> fullDTOList = new ArrayList<>();

        page
                .select(HtmlClassesToParseDNS.SPECIFICATION_CLASS.getClassName())
                .select(HtmlClassesToParseDNS.CHARACTERISTIC_GROUP_CLASS.getClassName())
                .stream()
                .filter(Objects::nonNull)
                .forEach(group -> fullDTOList.add(FullDTO.builder()
                        .name(group.select(HtmlClassesToParseDNS
                                .CHARACTERISTIC_NAME_CLASS.getClassName()).text())
                        .list(group.select(HtmlClassesToParseDNS.PARAM_CLASS.getClassName())
                                .stream()
                                .map(parameter -> ParamDTO.builder()
                                .name(Objects.requireNonNull(parameter
                                        .selectFirst(HtmlClassesToParseDNS
                                                .PARAM_NAME_CLASS
                                                .getClassName()))
                                        .text())
                                .value(Objects.requireNonNull(parameter
                                        .selectFirst(HtmlClassesToParseDNS
                                                .PARAM_VALUE_CLASS
                                                .getClassName()))
                                        .text())
                                .build()).toList())
                        .build()
                ));
        specificationsDTO.setFull(fullDTOList);
        return specificationsDTO;
    }

    private List<Parameter> toParameters(List<FullDTO> fullsDTO) {
        return fullsDTO.stream()
                .flatMap(fullDTO -> {
                    Characteristic characteristic = toCharacteristic(fullDTO.getName());
                    return fullDTO.getList().stream()
                            .map(paramDTO -> new Parameter()
                                    .setName(paramDTO.getName())
                                    .setValue(paramDTO.getValue())
                                    .setCharacteristic(characteristic)
                            );
                })
                .toList();

    }

    @Override
    public Characteristic toCharacteristic(String name) {
        return switch (name) {
            case "Общие параметры", "Основные параметры"
                    -> new Characteristic().setName("Основные характеристики");
            case "Вывод изображения", "Спецификации видеопроцессора", "Спецификации видеопамяти"
                    -> new Characteristic().setName("Технические характеристики");
            case "Подключение" -> new Characteristic().setName("Разъемы");
            case "Система охлаждения" -> new Characteristic().setName("Комплектация");
            case "Габариты и вес" -> new Characteristic().setName("Размер и вес");
            default -> new Characteristic().setName("Дополнительные характеристики");
        };
    }
}