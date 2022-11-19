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
import ru.mephi.gpus_agrgtr.parser.videocards.dns.enumerations.HtmlClasses;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.response.FullDTO;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.response.ParamDTO;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.response.SpecificationsDTO;
import ru.mephi.gpus_agrgtr.utils.ListUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                            List.of(HtmlClasses.SPECIFICATION.getName()));
                    products.add(getProduct(page, costs.get(i), links.get(i), names.get(i)));
                    log.info("Product with link :{} was handled", links.get(i));
                } catch (RuntimeException e) {
                    log.info("Product with link [" + links.get(i) + "] was not created." + e.getMessage());
                }
            }
            if (numberOfPage <= countPages) {
                page = getPageWithDefaultWait(String.format(storeUrl, numberOfPage));
            }
        }
        return products;
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

    private int getCountPages(Document page) {
        String countPages = Optional
                .ofNullable(page.select(HtmlClasses.PAGINATION_ELEMENT.getName()).last())
                .orElseThrow(() -> new RuntimeException("Unable to load pagination element"))
                .attr(HtmlClasses.PAGINATION_ATTRIBUTE.getName());
        return Integer.parseInt(countPages);
    }

    private List<Double> getCosts(Document page) {
        return page.select(HtmlClasses.COST.getName())
                .select(HtmlClasses.SUB_FOR_COSTS.getName())
                .stream()
                .filter(Objects::nonNull)
                .map(Element::text)
                .map(textWithCost -> getByPattern(NUMBER, textWithCost))
                .map(Double::parseDouble)
                .toList();
    }

    private List<String> getNames(Document page) {
        return page.select(HtmlClasses.LINK.getName()).stream()
                .filter(Objects::nonNull)
                .map(Element::text)
                .map(title -> getByPattern(NAME, title))
                .map(name -> name.trim().toUpperCase())
                .map(DnsParser::getReplacedName)
                .toList();
    }

    public static String getReplacedName(String name) {
        Pattern pattern = Pattern.compile(NAME_BRACKETS);
        Matcher matcher = pattern.matcher(name);
        int count = 0;
        while (matcher.find()) {
            if (count >= 3) {
                break;
            }
            name = switch (++count) {
                case 1 -> {
                    char[] chars = name.toCharArray();
                    chars[matcher.start()] = '(';
                    yield String.valueOf(chars);
                }
                case 2 -> {
                    char[] chars = name.toCharArray();
                    chars[matcher.start()] = ')';
                    yield String.valueOf(chars);
                }
                case 3 -> name.substring(0, matcher.start()).trim();
                default -> name;
            };
        }
        return name;
    }

    private List<String> getLinks(Document page) {
        return page.select(HtmlClasses.LINK.getName()).stream()
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

    public void checkDownloaded(List<String> classElements) {
        boolean checked = delayControl(classElements);
        if (!checked) {
            throw new RuntimeException("Unable to load page");
        }
    }

    private boolean delayControl(List<String> classElementsToCheck) {
        Duration current = Duration.ofMillis(START_WAIT_TIME_AS_MILLIS_ELEMENT);
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, current);
        AtomicReference<Boolean> isInvisible = new AtomicReference<>(false);
        Boolean isVisible = true;
        ArrayList<String> list = new ArrayList<>();
        int count = 0;
        do {
            WebDriverWait finalWebDriverWait = webDriverWait;
            classElementsToCheck.parallelStream().forEach(classElement -> {
                isInvisible.set(finalWebDriverWait.until(ExpectedConditions.invisibilityOfElementLocated
                        (By.className(classElement))));
                if (isInvisible.get()) {
                    list.add(classElement);
                    isInvisible.set(false);
                }
            });
            if (!list.isEmpty()) {
                for (String s : list) {
                    if (isVisible) {
                        isVisible = webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated
                                (By.className(s)));
                    } else {
                        webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated
                                (By.className(s)));
                    }
                }
            }
            webDriverWait = new WebDriverWait(webDriver, current.plus(Duration.ofSeconds(EXTRA_TIME_AS_SECONDS)));
            count++;
        } while (!isVisible || count != 3);
        return isVisible;
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
                .setStores(ListUtils.of(store))
                .setParameters(toParameters(specificationsDTO.getFull()));
    }

    private SpecificationsDTO requestData(Document page) {
        SpecificationsDTO specificationsDTO = new SpecificationsDTO();
        List<FullDTO> fullDTOList = new ArrayList<>();
        page
                .select(HtmlClasses.SPECIFICATION.getName())
                .select(HtmlClasses.CHARACTERISTIC_GROUP.getName())
                .stream()
                .filter(Objects::nonNull)
                .forEach(group -> fullDTOList.add(getFullDTO(group)));

        specificationsDTO.setFull(fullDTOList);
        return specificationsDTO;
    }

    private FullDTO getFullDTO(Element groupElement) {
        return FullDTO.builder()
                .name(groupElement.select(HtmlClasses
                        .CHARACTERISTIC_NAME.getName()).text())
                .list(groupElement.select(HtmlClasses.SPECIFICATION_PARAM.getName())
                        .stream()
                        .map(this::getParamDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private ParamDTO getParamDTO(Element parameter) {
        return ParamDTO.builder()
                .name(Objects.requireNonNull(parameter
                                .selectFirst(HtmlClasses
                                        .PARAM_NAME
                                        .getName()))
                        .text())
                .value(Objects.requireNonNull(parameter
                                .selectFirst(HtmlClasses
                                        .PARAM_VALUE
                                        .getName()))
                        .text())
                .build();
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
                .collect(Collectors.toList());
    }

    @Override
    public Characteristic toCharacteristic(String name) {
        return switch (name) {
            case "Общие параметры", "Основные параметры" -> new Characteristic().setName("Основные характеристики");
            case "Вывод изображения", "Спецификации видеопроцессора", "Спецификации видеопамяти" -> new Characteristic().setName("Технические характеристики");
            case "Подключение" -> new Characteristic().setName("Разъемы");
            case "Система охлаждения" -> new Characteristic().setName("Комплектация");
            case "Габариты и вес" -> new Characteristic().setName("Размер и вес");
            default -> new Characteristic().setName("Дополнительные характеристики");
        };
    }
}
