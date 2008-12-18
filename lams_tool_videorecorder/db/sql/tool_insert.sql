-- CVS ID: $Id$
 
INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
learning_library_id,
default_tool_content_id,
valid_flag,
grouping_support_type_id,
supports_run_offline_flag,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
define_later_url,
export_pfolio_learner_url,
export_pfolio_class_url,
contribute_url,
moderation_url,
help_url,
language_file,
classpath_addition,
context_file,
create_date_time,
modified_date_time,
supports_outputs
)
VALUES
(
'lavidr10',
'videoRecorderService',
'VideoRecorder',
'VideoRecorder',
'videoRecorder',
'@tool_version@',
NULL,
NULL,
0,
2,
1,
'tool/lavidr10/learning.do?mode=learner',
'tool/lavidr10/learning.do?mode=author',
'tool/lavidr10/learning.do?mode=teacher',
'tool/lavidr10/authoring.do',
'tool/lavidr10/monitoring.do',
'tool/lavidr10/authoring.do?mode=teacher',
'tool/lavidr10/exportPortfolio?mode=learner',
'tool/lavidr10/exportPortfolio?mode=teacher',
'tool/lavidr10/contribute.do',
'tool/lavidr10/moderate.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/lavidr10',
'org.lamsfoundation.lams.tool.videoRecorder.ApplicationResources',
'lams-tool-lavidr10.jar',
'/org/lamsfoundation/lams/tool/videoRecorder/videoRecorderApplicationContext.xml',
NOW(),
NOW(),
1
)
