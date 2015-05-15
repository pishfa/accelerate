package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.ui.controller.UiControllerOption;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public enum EntityControllerOption implements UiControllerOption {

    /**
     * Integer, indicates the page size
     */
	PAGE_SIZE,
    /**
     * Boolean, indicates that the controller can add entities
     */
	ADD,
    /**
     * Boolean, indicates that the controller can edit entities
     */
	EDIT,
    /**
     * Boolean, indicates that the controller can delete entities
     */
	DELETE,
    /**
     * Boolean.
     */
	MULTI_SELECT,
    /**
     * String, Name of id parameter
     */
    ID,
    /**
     * Integer or Boolean, In tree views, this indicates how many levels to be fetched lazily for others just a boolean indicates
     * that the data is kept in controller locally.
     */
	LOCAL,
    /**
     * String, default outcome of controller ui actions
     */
    OUTCOME,
    /**
     * String, the security action that filtering should be based on it.
     */
    VIEW_ACTION,
    /**
     * Boolean, the sorting direction
     */
    SORT_ASCENDING,
    /**
     * String, the path to the fields which sorting should be based on them. The path usually starts with "e." which refers
     *         to the current entity. Multiple field must be separated by comma.
     */
    SORT_ON,
    /**
     * Boolean, if true, security will be checked else no security check will be performed. By default, its value is determined from security configuration.
     */
    SECURED,
    /**
     * Boolean, if true, the current will be reattached upon every postback. The default is false.
     */
    AUTO_RELOAD
    ;
}
