package br.org.rnp.pastebinminerator.minner;

import java.net.ConnectException;
import java.util.List;
import java.util.regex.Matcher;

import br.org.rnp.pastebinminerator.entity.RegularExpression;
import br.org.rnp.pastebinminerator.entity.enums.ReasonType;
import br.org.rnp.pastebinminerator.entity.enums.Status;
import br.org.rnp.pastebinminerator.entity.json.Paste;
import br.org.rnp.pastebinminerator.entity.json.PasteEntity;
import br.org.rnp.pastebinminerator.entity.json.Reason;
import br.org.rnp.pastebinminerator.util.KeyWordsLoader;
import br.org.rnp.pastebinminerator.util.LanguageDetector;
import br.org.rnp.pastebinminerator.util.Log;
import br.org.rnp.pastebinminerator.util.RegularExpressionsLoader;
import br.org.rnp.pastebinminerator.util.Util;

public class DataManage extends Thread {
    private static int numberOfExecutions = 1;
    private Integer numberOfPastesToColect;
    private List<String> languages;

    public DataManage(Integer numberOfPastesToColect, List<String> languages) {
        this.numberOfPastesToColect = numberOfPastesToColect;
        this.languages = languages;
    }

    @Override
    public void run() {
        final int executionNumber = DataManage.numberOfExecutions++;
        Log.log("Starting execution #" + executionNumber + ".");
        DataColector dc = new DataColector();

        List<Paste> pastes;

        try {
            Log.log("Collecting most recent pastes (" + executionNumber + ").", true);
            pastes = dc.collectMostRecents(this.numberOfPastesToColect);
            if (pastes == null || pastes.isEmpty()) {
                Log.log("No recent pastes retrieved to execution number " + executionNumber + ".",
                        this.getClass().getName());
                return;
            }

            Log.log("Processing the " + pastes.size() + " pastes (" + executionNumber + ")");
            proccess(pastes);

        } catch (ConnectException e) {
            Log.log(e.toString() + "\n at execution number " + executionNumber + ".", this.getClass().getName());
        }

        Log.log("Ending execution (" + executionNumber + ").", true);
    }

    private void proccess(List<Paste> pastes) {
        for (Paste paste : pastes) {
            assignLanguage(paste);
            assignRegularExpressions(paste);
            assignKeyWords(paste);
            DataSaver.getInstance().add(paste);
        }
    }

    private void assignKeyWords(Paste paste) {
        for (String keyWord : KeyWordsLoader.getAcceptList()) {
            if (paste.getText().contains(keyWord)) {
                Reason reason = new Reason(Util.PREFIX_KEYWORD + keyWord, ReasonType.KEYWORD, Status.ACCEPT);
                paste.getReasons().add(reason);
            }
        }

        for (String keyWord : KeyWordsLoader.getBlackList()) {
            if (paste.getText().contains(keyWord)) {
                Reason reason = new Reason(Util.PREFIX_KEYWORD + keyWord, ReasonType.KEYWORD, Status.REGECT);
                paste.getReasons().add(reason);
            }
        }

    }

    private void assignRegularExpressions(Paste paste) {
        assignListOfRegularExpressions(paste, RegularExpressionsLoader.getAcceptedBy(), Status.ACCEPT);
        assignListOfRegularExpressions(paste, RegularExpressionsLoader.getBlackList(), Status.REGECT);
    }

    private void assignListOfRegularExpressions(Paste paste, List<RegularExpression> listRegEx, Status status) {
        for (RegularExpression r : listRegEx) {
            processSingleRegex(paste, r, status);
        }
    }

    private void processSingleRegex(Paste paste, RegularExpression r, Status status) {
        Reason reason = null;
        Matcher matcher = r.getExpression().matcher(paste.getText());
        while (matcher.find()) {
            if (reason == null) {
                reason = new Reason(Util.PREFIX_REGEX+ r.getName(), ReasonType.REGEX, status);
            }
            //System.out.println("#creating entity: reason = " + reason.getReason());
            //System.out.println("#creating entity: group = " + matcher.group());
            paste.getEntities().add(new PasteEntity(paste, reason, matcher.group()));
        }
        if (reason != null) {
            paste.getReasons().add(reason);
            paste.addRelevancy(status == Status.ACCEPT ? r.getWeight() : -r.getWeight());
        }
    }

    private void assignLanguage(Paste paste) {
        Reason reason;
        String language = LanguageDetector.getLanguage(paste.getText());
        if (language != null) {
            if (languages != null && languages.contains(language)) {
                reason = new Reason(Util.PREFIX_LANGUAGE + language, ReasonType.LANGUAGE, Status.ACCEPT);
            } else {
                reason = new Reason(Util.PREFIX_LANGUAGE + language, ReasonType.LANGUAGE, Status.REGECT);
            }
            paste.getReasons().add(reason);
        }
    }

    }