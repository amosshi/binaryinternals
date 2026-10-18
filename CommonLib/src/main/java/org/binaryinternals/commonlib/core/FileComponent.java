/*
 * ClassComponent.java    8:51 PM, August 7, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib.core;

/// Super class for all components in a class file. This class has two fields:
/// `startPos` indicates the start index in the class file byte array
/// of current component; `length` is
///
///
/// @author Amos Shi
public class FileComponent {

    /// Start position in the class file byte array of current component.
    @SuppressWarnings("ProtectedField")
    protected int startPos;
    /// Length of current component.
    @SuppressWarnings("ProtectedField")
    protected int length;

    public FileComponent() {
        this(0, 0);
    }

    /// Creates a file component of the given position and length.
    ///
    /// @param startPos Start position in the file byte array
    /// @param length Length of the component
    protected FileComponent(final int startPos, final int length) {
        this.startPos = startPos;
        this.length = length;
    }

    /// Get the start position of current component.
    ///
    /// @return The start position
    public int getStartPos() {
        return this.startPos;
    }

    /// Get the length of current component.
    ///
    /// @return The length
    public int getLength() {
        return this.length;
    }
}
