package lazyElement;

import java.util.ArrayList;
import java.util.List;

import io.appium.java_client.windows.WindowsDriver;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/// <summary>
/// Driver for dynamically finding and interacting with elements
/// </summary>
public class LazyElement extends AbstractLazyWebElement {

    /// <summary>
    /// Initializes a new instance of the <see cref="LazyElement" /> class
    /// </summary>
    /// <param name="testObject">The base Selenium test object</param>
    /// <param name="locator">The 'by' selector for the element</param>
    /// <param name="userFriendlyName">A user friendly name, for logging
    /// purposes</param>
    public LazyElement(Logger log, WindowsDriver<?> webDriver, By locator, String userFriendlyName) {
        super(log, webDriver, locator, userFriendlyName);
    }

    /// <summary>
    /// Initializes a new instance of the <see cref="LazyElement" /> class
    /// </summary>
    /// <param name="parent">The parent lazy element</param>
    /// <param name="locator">The 'by' selector for the element</param>
    /// <param name="userFriendlyName">A user friendly name, for logging
    /// purposes</param>
    public LazyElement(Logger log, LazyElement parent, By locator, String userFriendlyName) {
        super(log, parent, locator, userFriendlyName);
    }

    public LazyElement(Logger log, LazyElement parent, By locator, WebElement element, int index, String userFriendlyName) {
        super(log, parent, locator, element, index, userFriendlyName);
    }

    /// <summary>
    /// Finds the first IWebElement using the given method.
    /// </summary>
    /// <param name="by">The locating mechanism to use</param>
    /// <returns>The first matching OpenQA.Selenium.IWebElement on the current
    /// context</returns>
    @Override
    public LazyElement findElement(By by, String userFriendlyName) throws Exception {
        return new LazyElement(this.getLogger(), this, by, userFriendlyName);
    }

    /// <summary>
    /// Finds the first IWebElement using the given method.
    /// </summary>
    /// <param name="by">The locating mechanism to use</param>
    /// <returns>The first matching OpenQA.Selenium.IWebElement on the current
    /// context</returns>
    @Override
    public LazyElement findElement(By by) {
        return new LazyElement(this.getLogger(), this, by, by.toString());
    }

    /// <summary>
    /// Finds all IWebElements within the current context using the given mechanism.
    /// </summary>
    /// <param name="by">The locating mechanism to use</param>
    /// <returns>All web elements matching the current criteria, or an empty list if
    /// nothing matches</returns>
    public List<WebElement> findElements(By by, String userFriendlyName) throws Exception {
        int index = 0;
        List<WebElement> elements = new ArrayList<WebElement>();
        for (WebElement element : this.getNewElement().findElements(by)) {
            elements.add(new LazyElement(this.getLogger(), this, by, element, index, userFriendlyName + " - " + index++));
        }

        return elements;
    }

    /// <summary>
    /// Finds all IWebElements within the current context using the given mechanism.
    /// </summary>
    /// <param name="by">The locating mechanism to use</param>
    /// <returns>All web elements matching the current criteria, or an empty list if
    /// nothing matches</returns>
    public List<WebElement> findElements(By by) {
        try {
            return this.findElements(by, by.toString());
        } catch (Exception e) {
            return null;
        }
    }
}