package ruby.keyboardwarrior.logic;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ruby.keyboardwarrior.commands.CommandResult;
import ruby.keyboardwarrior.commands.*;
import ruby.keyboardwarrior.common.Messages;
import ruby.keyboardwarrior.data.TasksList;
import ruby.keyboardwarrior.data.task.*;
import ruby.keyboardwarrior.storage.StorageFile;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static ruby.keyboardwarrior.common.Messages.*;


public class LogicTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();

    private StorageFile saveFile;
    private TasksList tasksList;
    private Logic logic;

    @Before
    public void setup() throws Exception {
        saveFile = new StorageFile(saveFolder.newFile("testSaveFile.txt").getPath());
        tasksList = new TasksList();
        saveFile.save(tasksList);
        logic = new Logic(saveFile, tasksList);
    }

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandBehavior(invalidCommand,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    /**
     * Executes the command and confirms that the result message is correct.
     * Both the 'address book' and the 'last shown list' are expected to be empty.
     * @see #assertCommandBehavior(String, String, TasksList, boolean, List)
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedMessage, TasksList.empty(),false, Collections.emptyList());
    }

    /**
     * Executes the command and confirms that the result message is correct and
     * also confirms that the following three parts of the Logic object's state are as expected:<br>
     *      - the internal address book data are same as those in the {@code expectedAddressBook} <br>
     *      - the internal 'last shown list' matches the {@code expectedLastList} <br>
     *      - the storage file content matches data in {@code expectedAddressBook} <br>
     */
    private void assertCommandBehavior(String inputCommand,
                                      String expectedMessage,
                                      TasksList expectedTasksList,
                                      boolean isRelevantTasksExpected,
                                      List<TodoTask> lastShownList) throws Exception {

        //Execute the command
        CommandResult r = logic.execute(inputCommand);

        //Confirm the result contains the right data
        assertEquals(expectedMessage, r.feedbackToUser);
        assertEquals(r.getRelevantTasks().isPresent(), isRelevantTasksExpected);
        if(isRelevantTasksExpected){
            assertEquals(lastShownList, r.getRelevantTasks().get());
        }

        //Confirm the state of data is as expected
        assertEquals(expectedTasksList, tasksList);
        assertEquals(lastShownList, logic.getLastShownList());
//        assertEquals(tasksList, saveFile.load());
    }


    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        assertCommandBehavior(unknownCommand, HelpCommand.MESSAGE_ALL_USAGES);
    }

    @Test
    public void execute_help() throws Exception {
        assertCommandBehavior("help", HelpCommand.MESSAGE_ALL_USAGES);
    }

    @Test
    public void execute_exit() throws Exception {
        assertCommandBehavior("exit", ExitCommand.MESSAGE_EXIT_ACKNOWEDGEMENT);
    }

    @Test
    public void execute_clear() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        tasksList.addTask(helper.generateTask(1));
        tasksList.addTask(helper.generateTask(2));
        tasksList.addTask(helper.generateTask(3));

        assertCommandBehavior("clear", ClearCommand.MESSAGE_SUCCESS, TasksList.empty(), false, Collections.emptyList());
    }

/*    @Test
    public void execute_add_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "add wrong args wrong args", expectedMessage);
        assertCommandBehavior(
                "add Valid Name 12345 e/valid@email.butNoPhonePrefix a/valid, address", expectedMessage);
        assertCommandBehavior(
                "add Valid Name p/12345 valid@email.butNoPrefix a/valid, address", expectedMessage);
        assertCommandBehavior(
                "add Valid Name p/12345 e/valid@email.butNoAddressPrefix valid, address", expectedMessage);
    }*/

/*    @Test
    public void execute_add_invalidTaskData() throws Exception {
        assertCommandBehavior(
                "add []\\[;] p/12345 e/valid@e.mail a/valid, address", TaskDetails.MESSAGE_DETAILS_CONSTRAINTS);
    }*/

    @Test
    public void execute_add_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TodoTask toBeAdded = helper.aTasks();
        TasksList expectedAB = new TasksList();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(toBeAdded),
                              String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                              expectedAB,
                              false,
                              Collections.emptyList());

    }

    /*@Test
    public void execute_view_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        assertCommandBehavior("view ", expectedMessage);
        assertCommandBehavior("view arg not number", expectedMessage);
    }*/

    /*@Test
    public void execute_view_invalidIndex() throws Exception {
        assertInvalidIndexBehaviorForCommand("view");
    }*/

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single person in the last shown list, using visible index.
     * @param commandWord to test assuming it targets a single person in the last shown list based on visible index.
     */
    private void assertInvalidIndexBehaviorForCommand(String commandWord) throws Exception {
        String expectedMessage = Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
        TestDataHelper helper = new TestDataHelper();
        TodoTask p1 = helper.generateTask(1);
        TodoTask p2 = helper.generateTask(2);
        List<TodoTask> lastShownList = helper.generateTaskList(p1, p2);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior(commandWord + " -1", expectedMessage, TasksList.empty(), false, lastShownList);
        assertCommandBehavior(commandWord + " 0", expectedMessage, TasksList.empty(), false, lastShownList);
        assertCommandBehavior(commandWord + " 3", expectedMessage, TasksList.empty(), false, lastShownList);

    }

    /*@Test
    public void execute_view_onlyShowsNonPrivate() throws Exception {

        TestDataHelper helper = new TestDataHelper();
        Task p1 = helper.generateTask(1);
        Task p2 = helper.generateTask(2);
        List<Task> lastShownList = helper.generateTaskList(p1, p2);
        TasksList expectedAB = helper.generateTasksList(lastShownList);
        helper.addToTasksList(tasksList, lastShownList);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior("view 1",
                              String.format(ViewCommand.MESSAGE_VIEW_TASK_DETAILS, p1.toString()),
                              expectedAB,
                              false,
                              lastShownList);

        assertCommandBehavior("view 2",
                              String.format(ViewCommand.MESSAGE_VIEW_TASK_DETAILS, p2.toString()),
                              expectedAB,
                              false,
                              lastShownList);
    }*/

    /*@Test
    public void execute_tryToViewMissingTask_errorMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task p1 = helper.generateTask(1);
        Task p2 = helper.generateTask(2);
        List<Task> lastShownList = helper.generateTaskList(p1, p2);

        TasksList expectedAB = new TasksList();
        expectedAB.addTask(p2);

        tasksList.addTask(p2);
        logic.setLastShownList(lastShownList);

        assertCommandBehavior("view 1",
                              Messages.MESSAGE_TASK_NOT_IN_TASKSLIST,
                              expectedAB,
                              false,
                              lastShownList);
    }*/

    /*@Test
    public void execute_viewAll_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        assertCommandBehavior("viewall ", expectedMessage);
        assertCommandBehavior("viewall arg not number", expectedMessage);
    }*/

    /*@Test
    public void execute_viewAll_invalidIndex() throws Exception {
        assertInvalidIndexBehaviorForCommand("viewall");
    }*/

    /*@Test
    public void execute_viewAll() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task p1 = helper.generateTask(1);
        Task p2 = helper.generateTask(2);
        List<Task> lastShownList = helper.generateTaskList(p1, p2);
        TasksList expectedAB = helper.generateTasksList(lastShownList);
        helper.addToTasksList(tasksList, lastShownList);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior("viewall 1",
                            String.format(ViewCommand.MESSAGE_VIEW_TASK_DETAILS, p1.toString()),
                            expectedAB,
                            false,
                            lastShownList);

        assertCommandBehavior("viewall 2",
                            String.format(ViewCommand.MESSAGE_VIEW_TASK_DETAILS, p2.toString()),
                            expectedAB,
                            false,
                            lastShownList);
    }*/

    /*@Test
    public void execute_tryToViewAllTaskMissingInTasksList_errorMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task p1 = helper.generateTask(1);
        Task p2 = helper.generateTask(2);
        List<Task> lastShownList = helper.generateTaskList(p1, p2);

        TasksList expectedAB = new TasksList();
        expectedAB.addTask(p1);

        tasksList.addTask(p1);
        logic.setLastShownList(lastShownList);

        assertCommandBehavior("viewall 2",
                                Messages.MESSAGE_TASK_NOT_IN_TASKSLIST,
                                expectedAB,
                                false,
                                lastShownList);
    }*/

    @Test
    public void execute_delete_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertCommandBehavior("delete ", expectedMessage);
        assertCommandBehavior("delete arg not number", expectedMessage);
    }

    @Test
    public void execute_delete_invalidIndex() throws Exception {
        assertInvalidIndexBehaviorForCommand("delete");
    }

    @Test
    public void execute_delete_removesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        TodoTask p1 = helper.generateTask(1);
        TodoTask p2 = helper.generateTask(2);
        TodoTask p3 = helper.generateTask(3);

        List<TodoTask> threeTasks = helper.generateTaskList(p1, p2, p3);

        TasksList expectedAB = helper.generateTasksList(threeTasks);
        expectedAB.removeTask(p2);


        helper.addToTasksList(tasksList, threeTasks);
        logic.setLastShownList(threeTasks);

        assertCommandBehavior("delete 2",
                String.format(DeleteCommand.MESSAGE_DELETE_ITEM_SUCCESS, p2),
                                expectedAB,
                                false,
                                threeTasks);
    }

    @Test
    public void execute_delete_missingInTasksList() throws Exception {

        TestDataHelper helper = new TestDataHelper();
        TodoTask p1 = helper.generateTask(1);
        TodoTask p2 = helper.generateTask(2);
        TodoTask p3 = helper.generateTask(3);

        List<TodoTask> threeTasks = helper.generateTaskList(p1, p2, p3);

        TasksList expectedAB = helper.generateTasksList(threeTasks);
        expectedAB.removeTask(p2);

        helper.addToTasksList(tasksList, threeTasks);
        tasksList.removeTask(p2);
        logic.setLastShownList(threeTasks);

        assertCommandBehavior("delete 4",
                                Messages.MESSAGE_TASK_NOT_IN_TASKSLIST,
                                expectedAB,
                                false,
                                threeTasks);
    }

    @Test
    public void execute_find_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior("find ", expectedMessage);
    }

    @Test
    public void execute_find_onlyMatchesFullWordsInNames() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        TodoTask pTarget1 = helper.generateTaskWithDetails("bla bla KEY bla");
        TodoTask pTarget2 = helper.generateTaskWithDetails("bla KEY bla bceofeia");
        TodoTask p1 = helper.generateTaskWithDetails("KE Y");
        TodoTask p2 = helper.generateTaskWithDetails("KEYKEYKEY sduauo");

        List<TodoTask> fourTasks = helper.generateTaskList(p1, pTarget1, p2, pTarget2);
        TasksList expectedAB = helper.generateTasksList(fourTasks);
        List<TodoTask> expectedList = helper.generateTaskList(pTarget1, pTarget2);
        helper.addToTasksList(tasksList, fourTasks);

        assertCommandBehavior("find KEY",
                                Command.getMessageForTasksListShownSummary(expectedList),
                                expectedAB,
                                true,
                                expectedList);
    }

    // Might need to re-look this test
    @Test
    public void execute_find_isNonCaseSensitive() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        TodoTask pTarget1 = helper.generateTaskWithDetails("bla bla KEY bla");
        TodoTask pTarget2 = helper.generateTaskWithDetails("bla KEY bla bceofeia");
        TodoTask p1 = helper.generateTaskWithDetails("key key");
        TodoTask p2 = helper.generateTaskWithDetails("KEy sduauo");

        List<TodoTask> fourTasks = helper.generateTaskList(p1, pTarget1, p2, pTarget2);
        TasksList expectedAB = helper.generateTasksList(fourTasks);
        List<TodoTask> expectedList = helper.generateTaskList(p1, pTarget1, p2, pTarget2);
        helper.addToTasksList(tasksList, fourTasks);

        assertCommandBehavior("find KEY",
                                Command.getMessageForTasksListShownSummary(expectedList),                            
                                expectedAB,
                                true,
                                expectedList);
    }

    @Test
    public void execute_find_matchesIfAnyKeywordPresent() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        TodoTask pTarget1 = helper.generateTaskWithDetails("bla bla KEY bla");
        TodoTask pTarget2 = helper.generateTaskWithDetails("bla rAnDoM bla bceofeia");
        TodoTask p1 = helper.generateTaskWithDetails("key key");
        TodoTask p2 = helper.generateTaskWithDetails("KEy sduauo");

        List<TodoTask> fourTasks = helper.generateTaskList(p1, pTarget1, p2, pTarget2);
        TasksList expectedAB = helper.generateTasksList(fourTasks);
        List<TodoTask> expectedList = helper.generateTaskList(p1, pTarget1, p2, pTarget2);
        helper.addToTasksList(tasksList, fourTasks);

        assertCommandBehavior("find KEY rAnDoM",
                                Command.getMessageForTasksListShownSummary(expectedList),
                                expectedAB,
                                true,
                                expectedList);
    }

    /**
     * A utility class to generate test data.
     */
    class TestDataHelper{

        TodoTask aTasks() throws Exception {
            TaskDetails taskdetails = new TaskDetails("This is a task");
            return new TodoTask(taskdetails);
        }

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the person data field values
         */
        TodoTask generateTask(int seed) throws Exception {
            return new TodoTask(new TaskDetails("Task " + seed));
        }

        /** Generates the correct add command based on the task given */
        String generateAddCommand(TodoTask p) {
            StringJoiner cmd = new StringJoiner(" ");

            cmd.add("add");

            cmd.add(p.getDetails().toString());
            return cmd.toString();
        }

        /**
         * Generates an TasksList based on the list of Tasks given.
         */
        TasksList generateTasksList(List<TodoTask> todoTasks) throws Exception{
            TasksList tasksList = new TasksList();
            addToTasksList(tasksList, todoTasks);
            return tasksList;
        }

        /**
         * Adds the given list of Tasks to the given TasksList
         */
        void addToTasksList(TasksList tasksList, List<TodoTask> tasksToAdd) throws Exception{
            for(TodoTask p: tasksToAdd){
                tasksList.addTask(p);
            }
        }

        /**
         * Creates a list of Tasks based on the given Task objects.
         */
        List<TodoTask> generateTaskList(TodoTask... tasks) throws Exception{
            List<TodoTask> taskList = new ArrayList<>();
            for(TodoTask p: tasks){
                taskList.add(p);
            }
            return taskList;
        }

        /**
         * Generates a Task object with given name. Other fields will have some dummy values.
         */
         TodoTask generateTaskWithDetails(String taskdetails) throws Exception {
            return new TodoTask(
                    new TaskDetails(taskdetails));
         }
    }

}
