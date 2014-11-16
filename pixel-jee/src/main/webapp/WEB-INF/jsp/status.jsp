
	<div class="copyspace">
            <div class="featuredProject">
                <h3>Pixel Web App Status</h3>

                <p>
                    <c:choose>
                        <c:when test="${initialized}">
                            Firmware Version: ${firmware}
                            <br />
                            Hardware Version: ${hardware}
                        </c:when>

                        <c:otherwise>
                            The Pixel is not initialized.
                            <br />
                        </c:otherwise>
                    </c:choose>
                    
                </p>
	    </div>
	</div>

