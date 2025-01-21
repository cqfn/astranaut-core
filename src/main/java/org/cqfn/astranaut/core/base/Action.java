/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.core.base;

/**
 * A node that represents an action that can be performed on another node.
 *  This type of nodes is necessary for the construction of difference trees.
 * @since 1.1.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public interface Action extends DiffTreeItem, PatternItem {
    /**
     * Converts a node reference to an action reference if the node is an action
     *  or the node prototype is an action.
     * @param node Node
     * @return Action or {@code null} if the node is not an action
     */
    static Action toAction(final Node node) {
        final Action action;
        if (node instanceof Action) {
            action = (Action) node;
        } else if (node instanceof PrototypeBasedNode) {
            action = Action.toAction(((PrototypeBasedNode) node).getPrototype());
        } else {
            action = null;
        }
        return action;
    }
}
