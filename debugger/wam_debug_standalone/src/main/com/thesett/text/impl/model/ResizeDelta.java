/*
 * © Copyright Rupert Smith, 2005 to 2013.
 *
 * ALL RIGHTS RESERVED. Any unauthorized reproduction or use of this
 * material is prohibited. No part of this work may be reproduced or
 * transmitted in any form or by any means, electronic or mechanical,
 * including photocopying, recording, or by any information storage
 * and retrieval system without express written permission from the
 * author.
 */
package com.thesett.text.impl.model;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ResizeDelta
{
    /**
     * Applies a delta to the console height.
     *
     * @param delta The pixel delta to apply.
     */
    void deltaResizeTop(int delta);
}
