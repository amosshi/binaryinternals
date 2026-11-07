package org.binaryinternals.format.pdf.basicobj;

import org.binaryinternals.commonlib.GenerateTreeNode;

/// A PDF basic object, see `PDF 32000-1:2008` `7.3`: Objects.
///
/// @author Amos Shi
public sealed interface BasicObject extends GenerateTreeNode
        permits Array, Boolean, Dictionary, Name, Null, Numeric, Reference, Stream, StringHexadecimal, StringLiteral {

    /// Get the start position of the object.
    ///
    /// @return The start position
    int getStartPos();

    /// Get the length of the object.
    ///
    /// @return The length
    int getLength();
}
