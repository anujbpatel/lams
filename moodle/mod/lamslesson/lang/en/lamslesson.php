<?php

// This file is part of Moodle - http://moodle.org/
//
// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.


/**
 * English strings for lamslesson
 *
 * You can have a rather longer description of the file as well,
 * if you like, and it can span multiple lines.
 *
 * @package   mod_lamslesson
 * @copyright 2011 LAMS Foundation - Ernie Ghiglione (ernieg@lamsfoundation.org) 
 * @license  http://www.gnu.org/licenses/gpl-2.0.html GNU GPL v2
 */

defined('MOODLE_INTERNAL') || die();

$string['modulename'] = 'LAMS Lesson';
$string['modulenameplural'] = 'LAMS Lessons';
$string['modulename_help'] = 'The LAMS Lesson module allows teachers to create LAMS lessons in Moodle.';
$string['modulename_link'] = 'lamslesson';
$string['lamslessonfieldset'] = 'Custom example fieldset';
$string['lamslessonname'] = 'Lesson name';
$string['lamslessonname_help'] = 'This is the content of the help tooltip associated with the lamslessonname field. Markdown syntax is supported.';
$string['lamslesson'] = 'LAMS Lesson';
$string['pluginadministration'] = 'LAMS Lesson administration';
$string['pluginname'] = 'LAMS Lesson';
$string['selectsequence'] = 'Select sequence';
$string['availablesequences'] = 'Sequences';
$string['openauthor'] = 'Author new LAMS lessons';

// Admin interface
$string['adminheader'] = 'LAMS Server Configuration';
$string['admindescription'] = 'Configure your LAMS server settings. Make <strong>sure</strong> that the values you enter here correspond with the once you already entered in your LAMS server. Otherwise the integration might not work.';

$string['serverurl'] = 'LAMS Server URL:';
$string['serverurlinfo'] = 'Here you need to enter the URL for your LAMS server. ie: http://localhost:8080/lams/.';

$string['serverid'] = 'Server ID:';
$string['serveridinfo'] = 'What is the Server ID you entered in your LAMS server?';

$string['serverkey'] = 'Server Key:';
$string['serverkeyinfo'] = 'What is the Server Key you entered in your LAMS server?';

$string['requestsource'] = 'Moodle instance name:';
$string['requestsourceinfo'] = 'What is the name of your Moodle instance?. This value will appear after saving a sequence and will be used to prompt the user to "return to <this value>". So here you can put the name you give your Moodle server. ie: "Virtual Campus"';

$string['validationbutton'] = "Validate settings";
$string['validationinfo'] = 'Press the button to validate your settings.';


//

$string['notsetup'] = 'Configuration is not complete';


// Labels for errors when calling LAMS Server
$string['restcall503'] = 'Call to LAMS failed: received an HTTP status of 503. This might mean that LAMS is unavailable. Please wait a minute and try again, or contact your system administrator.';
$string['restcall403'] = 'Call to LAMS failed: received an HTTP status of 403 Forbidden. Please check the configurations settings and/or contact your system administrator.';
$string['restcall400'] = 'Call to LAMS failed: received an HTTP status of 400 Bad Request. Please check the configurations settings and/or contact your system administrator.';
$string['restcall500'] = 'Call to LAMS failed: received an HTTP status of 500 Internal Error. Please check the configurations settings and/or contact your system administrator.';
$string['restcalldefault'] = 'Call to LAMS failed: received an HTTP status of: $a';
$string['restcallfail'] = 'Call to LAMS failed: received no response or connection was refused. Please check that you have the correct LAMS server URL and that it is online.';

$string['sequencenotselected'] = 'You must select a sequence to proceed.';

// view.php

$string['nolessons'] = 'There are no LAMS lessons yet in this instance.';
$string['lessonname'] = 'Lesson name';
$string['links'] = 'Links';
$string['introduction']  = 'Introduction';
$string['openmonitor'] = 'Monitor this lesson';
$string['lastmodified'] = 'Last modified';
$string['openlesson'] = 'Click here to open the lesson now';
$string['empty'] = 'empty';
