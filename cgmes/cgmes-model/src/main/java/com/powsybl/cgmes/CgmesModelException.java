package com.powsybl.cgmes;

import com.powsybl.commons.PowsyblException;

/*
 * #%L
 * CGMES data model
 * %%
 * Copyright (C) 2017 - 2018 RTE (http://rte-france.com)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public class CgmesModelException extends PowsyblException {
    public CgmesModelException(String message) {
        super(message);
    }

    public CgmesModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
