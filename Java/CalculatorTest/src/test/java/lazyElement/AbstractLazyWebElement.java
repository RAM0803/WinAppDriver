package lazyElement;


import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;



/// <summary>
/// Abstract structure for dynamically finding and interacting with elements
/// </summary>
public abstract class AbstractLazyWebElement implements Element {

    public static final int Retrytime = 1000;
    public static final int Timeout = 30000;

    /// <summary>
    /// The index in cases where the selector finds multiple elements
    /// </summary>
    private Integer intelementIndex;

    public Integer getIntelementIndex() {
        return intelementIndex;
    }

    private void setIntelementIndex(Integer intelementIndex) {
        this.intelementIndex = intelementIndex;
    }

    /// <summary>
    /// A user friendly name, for logging purposes
    /// </summary>
    private String userFriendlyName;

    public String getUserFriendlyName() {
        return userFriendlyName;
    }

    private void setUserFriendlyName(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    /// <summary>
    /// The parent lazy element
    /// </summary>
    private AbstractLazyWebElement parent;

    public AbstractLazyWebElement getParent() {
        return parent;
    }

    private void getParent(AbstractLazyWebElement parent) {
        this.parent = parent;
    }

    /// <summary>
    /// Gets a the 'by' selector for the element
    /// </summary>
    private By by;

    public By getBy() {
        return by;
    }

    private void setBy(By by) {
        this.by = by;
    }

    /// <summary>
    /// Gets the web driver
    /// </summary>
    private WebDriver webDriver;

    public WebDriver getDriver() {
        if (webDriver == null) {
            webDriver = getParent().getDriver();
        }
        return webDriver;
    }

    private void setDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /// <summary>
    /// Gets the logger
    /// </summary>
    private Logger log;

    public Logger getLogger() {
        return log;
    }

    private void setLogger(Logger log) {
        this.log = log;
    }

    /// <summary>
    /// Gets a cached copy of the element or null if we haven't already found the
    /// element
    /// </summary>
    public WebElement cachedElement;

    /// <summary>
    /// Initializes a new instance of the <see cref="AbstractLazyWebElement" />
    /// class
    /// </summary>
    /// <param name="testObject">The base test object</param>
    /// <param name="webDriver">The assoicated web driver</param>
    /// <param name="locator">The 'by' selector for the element</param>
    /// <param name="userFriendlyName">A user friendly name, for logging
    /// purposes</param>
    protected AbstractLazyWebElement(Logger logger, WebDriver webDriver, By locator, String userFriendlyName) {
        this.webDriver = webDriver;
        this.log = logger;
        this.by = locator;
        this.userFriendlyName = userFriendlyName;
    }

    /// <summary>
    /// Initializes a new instance of the <see cref="AbstractLazyWebElement" />
    /// class
    /// </summary>
    /// <param name="testObject">The base test object</param>
    /// <param name="webDriver">The assoicated web driver</param>
    /// <param name="locator">The 'by' selector for the element</param>
    /// <param name="userFriendlyName">A user friendly name, for logging
    /// purposes</param>
    protected AbstractLazyWebElement(Logger logger, AbstractLazyWebElement parentElement, By locator,
                                     String userFriendlyName) {
        this.parent = parentElement;
        this.log = logger;
        this.by = locator;
        this.userFriendlyName = userFriendlyName;
    }

    public AbstractLazyWebElement(Logger logger, AbstractLazyWebElement parentElement, By locator, WebElement element,
                                  int index, String userFriendlyName) {
        this.parent = parentElement;
        this.log = logger;
        this.by = locator;
        this.userFriendlyName = userFriendlyName;
        this.intelementIndex = index;
        this.cachedElement = element;
    }

    /// <summary>
    /// Click the lazy element
    /// </summary>
    @Override
    public void click() throws NoSuchElementException {
        this.log.debug("Click " + this.userFriendlyName);
        this.waitForElementNonNull();
        this.waitForClickAbleElement();
        try {
            GenericWait.wait(() -> {
                this.getElement().click();
                return true;
            }, Retrytime, Timeout, true);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Double clicks the lazy element
    /// </summary>
    public void clickAction() throws Throwable {
        Actions actions = new Actions(this.getDriver());
        actions.moveToElement(this.getElement()).click(this.getElement()).build().perform();
    }

    private boolean waitForClickAbleElement() {
        try {
            GenericWait.wait(() -> {
                WebElement element = getElement();
                if (element.isDisplayed() && element.isEnabled()) {
                    return true;
                } else {
                    getNewElement();
                    return false;
                }
            }, Retrytime, Timeout, true);
        } catch (Exception e) {
            throw new NoSuchElementException("Failed to find clickable element " + this.userFriendlyName + "\r\n" + e.getMessage());
        }
        return this.isEnabled();
    }

    /// <summary>
    /// Double clicks the lazy element
    /// </summary>
    public void doubleClick() throws Throwable {
        this.log.debug("Double clicking " + this.userFriendlyName);
        if (!this.waitForClickAbleElement()){
            throw new NoSuchElementException("Failed to find clickable element " + this.userFriendlyName);
        }
        try {
            GenericWait.wait(() -> {
                Actions actions = new Actions(this.getDriver());
                actions.moveToElement(this.getElement()).doubleClick(this.getElement()).build().perform();
                return true;
            }, Retrytime, Timeout, true);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Send keys to the lazy element
    /// </summary>
    /// <param name="text">The text to send to the lazy element</param>
    @Override
    public void sendKeys(CharSequence... keysToSend) throws NoSuchElementException {
        this.log.debug("Send text to " + keysToSend + " to " + this.userFriendlyName);
        try {
            GenericWait.wait(() -> {
                WebElement element = this.getElement();
                element.sendKeys(keysToSend);
                return true;
            }, Retrytime, Timeout, true);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Send Secret keys with no logging
    /// TODO: Implement
    /// </summary>
    /// <param name="keys">The keys to send</param>
    public void sendSecretKeys(String keys) {
        throw new NotImplementedException("Send secret keys not implemented");
    }

    /// <summary>
    /// Clear the lazy element
    /// </summary>
    public void clear() throws NoSuchElementException {
        this.log.debug("Send clear to " + this.userFriendlyName);
        try {
            GenericWait.wait(() -> {
                WebElement element = this.getElement();
                element.clear();
                return true;
            }, Retrytime, Timeout, false);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Submit the lazy element
    /// </summary>
    @Override
    public void submit() throws NoSuchElementException {
        this.log.debug("Send submit to " + this.userFriendlyName);
        try {
            GenericWait.wait(() -> {
                WebElement element = this.getElement();
                element.submit();
                return true;
            }, Retrytime, Timeout, true);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets the value for the given attribute
    /// </summary>
    /// <param name="attributeName">The given attribute name</param>
    /// <returns>The attribute value</returns>
    public String getAttribute(String attributeName) {
        this.log.debug("Getting attribute " + attributeName + " from element " + this.userFriendlyName);
        try {
            GenericWait.wait(() -> {
                this.getElement();
                return true;
            }, Retrytime, Timeout, true);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }

        try {
            return this.getElement().getAttribute(attributeName);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets the value for the given attribute
    /// </summary>
    /// <param name="attributeName">The given attribute name</param>
    /// <returns>The attribute value</returns>
    public String getText() {
        this.log.debug("Getting text from element " + this.userFriendlyName);
        try {
            GenericWait.wait(() -> {
                this.getElement();
                return true;
            }, Retrytime, Timeout, true);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }

        try {
            return this.getElement().getText();
        } catch (Exception e) {
            return null;
        }
    }

    /// <summary>
    /// Gets the current value of an element - Useful for get input box text
    /// </summary>
    /// <returns>The element's current value</returns>
    public String getValue() throws Exception {
        return this.getAttribute("Value");
    }

    /// <summary>
    /// Gets the CSS value for the given attribute
    /// </summary>
    /// <param name="propertyName">The given attribute/property name</param>
    /// <returns>The CSS value</returns>
    public String getCssValue(String propertyName) {
        this.log.debug("Getting css value " + propertyName + " from element " + this.userFriendlyName);
        waitForElementNonNull();
        try {
            return this.getElement().getCssValue(propertyName);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets the rectangle from the element
    /// </summary>
    /// <returns>The rectangle value</returns>
    public Rectangle getRect() {
        this.log.debug("Getting rectangle from element " + this.userFriendlyName);
        waitForElementNonNull();
        try {
            return this.getElement().getRect();
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets if the element is displayed
    /// </summary>
    /// <returns>True if the element is displayed</returns>
    @Override
    public boolean isDisplayed() {
        this.log.debug("Check to see if element is displayed: " + this.userFriendlyName);
        waitForElementNonNull();
        try {
            return getElement().isDisplayed();
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets if the element is displayed
    /// </summary>
    /// <returns>True if the element is displayed</returns>
    public <X> X getScreenshotAs(OutputType<X> target) {
        this.log.debug("Check to see if element is displayed: " + this.userFriendlyName);
        waitForElementNonNull();
        try {
            return getElement().getScreenshotAs(target);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets if the element is displayed
    /// </summary>
    /// <returns>True if the element is displayed</returns>
    public boolean isEnabled() throws NoSuchElementException {
        this.log.debug("Check to see if element is enabled: " + this.userFriendlyName);
        waitForElementNonNull();

        try {
            return this.getElement().isEnabled();
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets if the element is selected
    /// </summary>
    /// <returns>True if the element is selected</returns>
    public boolean isSelected() {
        this.log.debug("Check to see if element is enabled: " + this.userFriendlyName);
        waitForElementNonNull();

        try {
            return this.getElement().isSelected();
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets the element size();
    /// </summary>
    /// <returns>Element size</returns>
    public Dimension getSize() {
        this.log.debug("Check to see if element is enabled: " + this.userFriendlyName);
        waitForElementNonNull();

        try {
            return this.getElement().getSize();
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets the element size();
    /// </summary>
    /// <returns>Element size</returns>
    public String getTagName() {
        this.log.debug("Check to see if element is enabled: " + this.userFriendlyName);
        waitForElementNonNull();

        try {
            return this.getElement().getTagName();
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Gets the element size();
    /// </summary>
    /// <returns>Element size</returns>
    public Point getLocation() {
        this.log.debug("Check to see if element is enabled: " + this.userFriendlyName);
        waitForElementNonNull();

        try {
            return this.getElement().getLocation();
        } catch (Exception e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        }
    }

    /// <summary>
    /// Finds the first OpenQA.Selenium.WebElement using the given method.
    /// </summary>
    /// <param name="by">The locating mechanism to use</param>
    /// <returns>The first matching OpenQA.Selenium.WebElement on the current
    /// context</returns>
    public abstract WebElement findElement(By by, String userFriendlyName) throws Exception;

    /// <summary>
    /// Get a web element
    /// </summary>
    /// <param name="getElement">The get web element function</param>
    /// <returns>The web element</returns>
    protected WebElement getElement() throws NoSuchElementException {
        // Try to use cached element
        if (this.cachedElement != null) {
            try {
                // Do this to make sure we waited for the element to exist
                return this.cachedElement;
            } catch (Exception e) {
                log.debug("Re-finding element because: " + e.getMessage());
                getNewElement();
                return this.cachedElement;
            }
        }
        try {
            log.debug("Performing lazy driver find on: " + this.by);
            getNewElement();
            return this.cachedElement;
        } catch (Exception e) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Failed to find: " + this.userFriendlyName);
            messageBuilder.append("\r\nLocator: " + this.by);
            messageBuilder.append("\r\nBecause: " + e.getMessage());
            log.debug(messageBuilder.toString());
            throw new NoSuchElementException(messageBuilder.toString(), e);
        }
    }

    protected WebElement getNewElement() throws NoSuchElementException {
        DateTime start = DateTime.now();
        if (this.parent != null) {
            WebElement element = parent.getElement().findElement(by);
            this.cachedElement = element;
            DateTime end = DateTime.now();
            log.debug("Took " + (end.getMillisOfDay() - start.getMillisOfDay()) + " milliseconds to find " + getUserFriendlyName());
            return element;
        } else {
            WebElement element = this.webDriver.findElement(by);
            this.cachedElement = element;
            DateTime end = DateTime.now();
            log.debug("Took " + (end.getMillisOfDay() - start.getMillisOfDay()) + " milliseconds to find " + getUserFriendlyName());
            return element;
        }
    }

    /// <summary>
    /// Returns if the element is contained within the DOM
    /// </summary>
    /// <returns>True if the element is locatable in the dom</returns>
    private boolean waitForElementNonNull() {
        try {
            return GenericWait.wait(() -> {
                try {
                    return this.getElement() != null;
                } catch (Exception e) {
                    return false;
                }
            }, Retrytime, Timeout, false);
        } catch (Exception e) {
            log.debug("Failed to find element " + this.userFriendlyName);
            return false;
        }
    }
}