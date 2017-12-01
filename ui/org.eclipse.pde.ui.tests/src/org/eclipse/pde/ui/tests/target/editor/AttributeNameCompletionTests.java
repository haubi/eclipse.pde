/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Lucas Bullen (Red Hat Inc.)
 *******************************************************************************/
package org.eclipse.pde.ui.tests.target.editor;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Assert;

public class AttributeNameCompletionTests extends AbstractTargetEditorTest {
	public void testAttributeNameSuggestions() throws Exception {
		Map<Integer, String[]> expectedProposalsByOffset = new HashMap<>();
		// target
		expectedProposalsByOffset.put(8, new String[] { "name", "sequenceNumber" });
		// location
		expectedProposalsByOffset.put(33, new String[] { "includeAllPlatforms", "includeConfigurePhase", "includeMode",
				"includeSource", "type" });
		// unit
		expectedProposalsByOffset.put(41, new String[] { "id", "version" });
		// repository
		expectedProposalsByOffset.put(56, new String[] { "location" });
		// targetJRE
		expectedProposalsByOffset.put(95, new String[] { "path" });

		ITextViewer textViewer = getTextViewerForTarget("AttributeNamesTestCaseTarget");
		String text = textViewer.getDocument().get();
		int offset = 0;
		while (offset < text.length()) {
			int nextSpace = text.indexOf(' ', offset) + 1;
			int nextOpen = text.indexOf('<', offset);
			if (nextSpace == 0 && nextOpen == -1)
				break;
			if (nextSpace == 0) {
				offset = nextOpen;
			} else if (nextOpen == -1) {
				offset = nextSpace;
			} else {
				offset = Math.min(nextSpace, nextOpen);
			}

			ICompletionProposal[] completionProposals = contentAssist.computeCompletionProposals(textViewer,
					offset);
			if (expectedProposalsByOffset.containsKey(offset)) {
				checkProposals(expectedProposalsByOffset.get(offset), completionProposals, offset);
			} else {
				assertTrue("There should not be any proposals at index " + offset + ". Following proposals found: "
						+ proposalListToString(completionProposals), completionProposals.length == 0);
			}
			offset++;
		}
	}

	public void testNoAttributeNameRepeatSuggestions() throws Exception {
		ITextViewer textViewer = getTextViewerForTarget("AttributeNamesFullTestCaseTarget");
		String text = textViewer.getDocument().get();
		int offset = 0;
		while (offset < text.length()) {
			int nextSpace = text.indexOf("  ", offset) + 1;
			int nextOpen = text.indexOf('<', offset);
			if (nextSpace == 0 && nextOpen == -1)
				break;
			if (nextSpace == 0) {
				offset = nextOpen;
			} else if (nextOpen == -1) {
				offset = nextSpace;
			} else {
				offset = Math.min(nextSpace, nextOpen);
			}

			ICompletionProposal[] completionProposals = contentAssist.computeCompletionProposals(textViewer,
					offset);
			if (completionProposals.length != 0) {
				Assert.fail("There should not be any proposals at index " + offset + ". Following proposals found: "
						+ proposalListToString(completionProposals));
			}
			offset++;
		}
	}
}