-- Create User
mysql -u root -p mysql
GRANT ALL PRIVILEGES ON *.* TO 'xper_rw'@'localhost' IDENTIFIED BY 'up2nite' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'xper_rw'@'%' IDENTIFIED BY 'up2nite' WITH GRANT OPTION;


-- Create Table
CREATE TABLE AcqData (
  tstamp bigint(20) NOT NULL default '0',
  data longblob NOT NULL,
  PRIMARY KEY  (tstamp)
) ENGINE=MyISAM MAX_ROWS=4294967295 AVG_ROW_LENGTH=16384 COMMENT='Timestamp: when the first data record is saved.';

CREATE TABLE AcqSession (
  start_time bigint(20) NOT NULL default '0',
  stop_time bigint(20) NOT NULL default '0',
  PRIMARY KEY  (start_time)
) ENGINE=MyISAM COMMENT='Timestamp: start and stop of each trial.';

CREATE TABLE BehMsg (
  tstamp bigint(20) NOT NULL default '0',
  type varchar(255) NOT NULL,
  msg longtext NOT NULL,
  PRIMARY KEY (tstamp, type)
) ENGINE=MyISAM;

CREATE TABLE `ExpLog` (
  `tstamp` bigint(20) NOT NULL DEFAULT '0',
  `dateTime` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'null',
  `status` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `type` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'null',
  `depth` bigint(20) NOT NULL DEFAULT '-1',
  `globalGenId` int(11) NOT NULL DEFAULT '-1',
  `localGenId` int(11) NOT NULL DEFAULT '-1',
  `firstGlobalGenId` int(11) NOT NULL DEFAULT '-1',
  `cellNum` bigint(20) NOT NULL DEFAULT '-1',
  `isRealExpt` tinyint(1) NOT NULL DEFAULT '0',
  `memo` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`tstamp`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Sach ExpRecTargets (Gizmo):
CREATE TABLE `ExpRecTargets` (
  `date` bigint(11) unsigned NOT NULL,
  `goodCheck` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `coordsBV_ML` int(11) NOT NULL,
  `coordsBV_AP` int(11) NOT NULL,
  `coordsBV_DV` int(11) NOT NULL,
  `coordsST_ML` int(11) NOT NULL,
  `coordsST_AP` int(11) NOT NULL,
  `coordsST_DV` int(11) NOT NULL,
  `coordsAS_azim` double NOT NULL,
  `coordsAS_elev` double NOT NULL,
  `coordsAS_dist` double NOT NULL,
  `notes` longtext COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`date`),
  KEY `date` (`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='This table saves the target for a day''s recording. The coordinates are saved in Brain Voyager (BV), Stereotaxic (ST), and Angular System (AS) reference frames. We also save a flag as to whether we found cells tuned for the trained behavioral stimuli (goodCheck) and notes.';


-- Andrew ExpRecTargets (Shaggy):
CREATE TABLE `ExpRecTargets` (
  `cellNum` bigint(20) NOT NULL DEFAULT '-1',
  `drivenDistance` double NOT NULL,
  `coordsST_ML` double NOT NULL,
  `coordsST_AP` double NOT NULL,
  `coordsST_DV` double NOT NULL,
  `coordsAS_azim` double NOT NULL,
  `coordsAS_elev` double NOT NULL,
  `coordsAS_dist` double NOT NULL,
  `notes` longtext COLLATE utf8_unicode_ci NOT NULL,
  `isFinished` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`cellNum`),
  KEY `cellNum` (`cellNum`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Record each cell (per row) here';

-- SessionLog (started for Shaggy):
CREATE TABLE `SessionLog` (
  `dateTime` bigint(20) NOT NULL DEFAULT '-1',
  `drivenDistance` double NOT NULL DEFAULT 0,
  `pivotDistance` double NOT NULL DEFAULT 0,
  `guideTubeDistance` double NOT NULL DEFAULT 0,
  `coordsST_ML` double NOT NULL,
  `coordsST_AP` double NOT NULL,
  `coordsST_DV` double NOT NULL,
  `coordsAS_azim` double NOT NULL,
  `coordsAS_elev` double NOT NULL,
  `coordsAS_dist` double NOT NULL,
  `notes` longtext COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`dateTime`),
  KEY `dateTime` (`dateTime`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='General purpose comments for each recording session';

CREATE TABLE `GenId_to_StimObjId` (
  `gen_id` bigint(11) NOT NULL DEFAULT '0',
  `stimObjId` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gen_id`,`stimObjId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE InternalState (
  CREATE TABLE `InternalState` (
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `arr_ind` int(11) NOT NULL DEFAULT '0',
  `val` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`name`,`arr_ind`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `OccludedSeries` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `SeriesId` int(11) DEFAULT NULL COMMENT 'Unique identifier for this set of parameters',
  `Category` int(11) DEFAULT NULL COMMENT 'the first stimulus displayed',
  `Order` int(11) DEFAULT NULL,
  `Node1` int(11) DEFAULT NULL COMMENT 'first node - the tail of the vector',
  `Node2` int(11) DEFAULT NULL COMMENT '2nd node is the head of the vector',
  `PerpDist` double DEFAULT NULL COMMENT 'perpendicular distance',
  `Loc1` double DEFAULT NULL COMMENT 'the second stimulus',
  `Loc2` double DEFAULT NULL COMMENT 'aperture inner radius',
  `Loc3` double DEFAULT NULL COMMENT 'aperture outer radius',
  `Loc4` double DEFAULT NULL,
  `Offset1` double DEFAULT NULL COMMENT 'the second stimulus',
  `Offset2` double DEFAULT NULL COMMENT 'aperture inner radius',
  `Offset3` double DEFAULT NULL COMMENT 'aperture outer radius',
  `Offset4` double DEFAULT NULL,
  `MorphMax` double DEFAULT NULL,
  `MorphMin` double DEFAULT NULL,
  `InnerRadius` double DEFAULT NULL,
  `OuterRadius` double DEFAULT NULL,
  `Center_x` double DEFAULT NULL,
  `Center_y` double DEFAULT NULL,
  `MorphLimb1` int(11) DEFAULT NULL,
  `MorphLimb2` int(11) DEFAULT NULL,
  `MorphLimb3` int(11) DEFAULT NULL,
  `MorphLimb4` int(11) DEFAULT NULL,
  `MorphLimb5` int(11) DEFAULT NULL,
  `MorphLimb6` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1470763164 DEFAULT CHARSET=latin1;

CREATE TABLE RFInfo (
  tstamp bigint(20) NOT NULL default '0',
  info longtext NOT NULL,
  PRIMARY KEY  (tstamp)
) ENGINE=MyISAM;

CREATE TABLE RFStimSpec (
  id bigint(20) NOT NULL default '0',
  spec longtext NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM;

CREATE TABLE Thumbnail (
  id bigint(20) NOT NULL default '0',
  data longblob NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM;

CREATE TABLE StimSpec (
  id bigint(20) NOT NULL default '0',
  spec longtext NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM;

CREATE TABLE TaskDone (
  tstamp bigint(20) NOT NULL default '0',
  task_id bigint(20) NOT NULL default '0',
  part_done tinyint(4) NOT NULL default '0',
  PRIMARY KEY  (tstamp)
) ENGINE=MyISAM COMMENT='Timestamp: when the stim is shown.';

CREATE TABLE TaskToDo (
  task_id bigint(20) NOT NULL default '0',
  stim_id bigint(20) NOT NULL default '0',
  xfm_id bigint(20) NOT NULL default '0',
  gen_id bigint(11) NOT NULL default '0',
  PRIMARY KEY  (task_id)
) ENGINE=MyISAM;

CREATE TABLE XfmSpec (
  id bigint(20) NOT NULL default '0',
  spec longtext NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM;

CREATE TABLE SystemVar (
  name varchar(255) NOT NULL default '',
  arr_ind int(11) NOT NULL default '0',
  tstamp bigint(20) NOT NULL default '0',
  val text NOT NULL,
  PRIMARY KEY  (tstamp,name,arr_ind),
  KEY name_arr_ind (name,arr_ind)
) ENGINE=MyISAM;

DROP TABLE IF EXISTS `StimObjData`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StimObjData` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `spec` longtext COLLATE utf8_unicode_ci NOT NULL,
  `data` longtext COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- Populate SystemVar table
INSERT INTO SystemVar VALUES ('xper_slide_length',0,1097009302111741,'750');
INSERT INTO SystemVar VALUES ('xper_inter_slide_interval',0,1097009302112036,'250');
INSERT INTO SystemVar VALUES ('xper_inter_trial_interval',0,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_delay_after_trial_complete',0,1097009302112331,'500');
INSERT INTO SystemVar VALUES ('xper_time_before_fixation_point_on',0,1097009302112331,'100');
INSERT INTO SystemVar VALUES ('xper_time_allowed_for_initial_eye_in',0,1097009302112331,'1000');
INSERT INTO SystemVar VALUES ('xper_required_eye_in_and_hold_time',0,1097009302112331,'500');
INSERT INTO SystemVar VALUES ('xper_slides_per_trial',0,1097009302112331,'4');
INSERT INTO SystemVar VALUES ('xper_do_empty_task',0,1097009302112331,'true');
INSERT INTO SystemVar VALUES ('xper_first_slide_length',0,1097009302111741,'5000');
INSERT INTO SystemVar VALUES ('xper_first_inter_slide_interval',0,1097009302112036,'5000');

INSERT INTO SystemVar VALUES ('xper_eye_window_center',0,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_eye_window_center',1,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_eye_window_algorithm_base_window_size',0,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_eye_window_algorithm_initial_window_size',0,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_eye_window_algorithm_ramp_length',0,1097009302112331,'10');

INSERT INTO SystemVar VALUES ('xper_left_iscan_channel',0,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_left_iscan_channel',1,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_left_iscan_channel_min_value',0,1097009302112331,'-10');
INSERT INTO SystemVar VALUES ('xper_left_iscan_channel_min_value',1,1097009302112331,'-10');
INSERT INTO SystemVar VALUES ('xper_left_iscan_channel_max_value',0,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_left_iscan_channel_max_value',1,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_left_iscan_channel_reference',0,1097009302112331,'diff');
INSERT INTO SystemVar VALUES ('xper_left_iscan_channel_reference',1,1097009302112331,'diff');

INSERT INTO SystemVar VALUES ('xper_left_iscan_eye_zero',0,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_left_iscan_eye_zero',1,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_left_iscan_eye_zero_update_enabled',0,1097009302112331,'true');
INSERT INTO SystemVar VALUES ('xper_left_iscan_eye_zero_algorithm_eye_window_threshold',0,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_left_iscan_eye_zero_algorithm_min_sample',0,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_left_iscan_eye_zero_algorithm_span',0,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_left_iscan_mapping_algorithm_parameter',0,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_left_iscan_mapping_algorithm_parameter',1,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_left_iscan_mapping_algorithm_parameter',2,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_left_iscan_mapping_algorithm_parameter',3,1097009302112331,'1');

INSERT INTO SystemVar VALUES ('xper_right_iscan_channel',0,1097009302112331,'2');
INSERT INTO SystemVar VALUES ('xper_right_iscan_channel',1,1097009302112331,'3');
INSERT INTO SystemVar VALUES ('xper_right_iscan_channel_min_value',0,1097009302112331,'-10');
INSERT INTO SystemVar VALUES ('xper_right_iscan_channel_min_value',1,1097009302112331,'-10');
INSERT INTO SystemVar VALUES ('xper_right_iscan_channel_max_value',0,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_right_iscan_channel_max_value',1,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_right_iscan_channel_reference',0,1097009302112331,'diff');
INSERT INTO SystemVar VALUES ('xper_right_iscan_channel_reference',1,1097009302112331,'diff');

INSERT INTO SystemVar VALUES ('xper_right_iscan_eye_zero',0,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_right_iscan_eye_zero',1,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_right_iscan_eye_zero_update_enabled',0,1097009302112331,'true');
INSERT INTO SystemVar VALUES ('xper_right_iscan_eye_zero_algorithm_eye_window_threshold',0,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_right_iscan_eye_zero_algorithm_min_sample',0,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_right_iscan_eye_zero_algorithm_span',0,1097009302112331,'10');
INSERT INTO SystemVar VALUES ('xper_right_iscan_mapping_algorithm_parameter',0,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_right_iscan_mapping_algorithm_parameter',1,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_right_iscan_mapping_algorithm_parameter',2,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_right_iscan_mapping_algorithm_parameter',3,1097009302112331,'1');

INSERT INTO SystemVar VALUES ('xper_eye_monitor_in_time_threshold',0,1097009302112331,'100');
INSERT INTO SystemVar VALUES ('xper_eye_monitor_out_time_threshold',0,1097009302112331,'100');
INSERT INTO SystemVar VALUES ('xper_eye_sampling_interval',0,1097009302112331,'10');

INSERT INTO SystemVar VALUES ('xper_monkey_screen_width',0,1097009302112331,'330');
INSERT INTO SystemVar VALUES ('xper_monkey_screen_height',0,1097009302112331,'208');
INSERT INTO SystemVar VALUES ('xper_monkey_screen_depth',0,1097009302112331,'6000');
INSERT INTO SystemVar VALUES ('xper_monkey_screen_distance',0,1097009302112331,'500');
INSERT INTO SystemVar VALUES ('xper_monkey_pupil_distance',0,1097009302112331,'50');
INSERT INTO SystemVar VALUES ('xper_monkey_screen_inverted',0,1097009302112331,'true');

INSERT INTO SystemVar VALUES ('xper_fixation_position',0,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_fixation_position',1,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_fixation_point_color',0,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_fixation_point_color',1,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_fixation_point_color',2,1097009302112331,'0');
INSERT INTO SystemVar VALUES ('xper_fixation_point_size',0,1097009302112331,'5');
INSERT INTO SystemVar VALUES ('xper_fixation_on_with_stimuli',0,1097009302112331,'true');

INSERT INTO SystemVar VALUES ('xper_screen_marker_size',0,1097009302112331,'20');
INSERT INTO SystemVar VALUES ('xper_screen_marker_viewport_index',0,1097009302112331,'0');

INSERT INTO SystemVar VALUES ('xper_time_allowed_for_initial_target_selection',0,1097009302112331,'1000');
INSERT INTO SystemVar VALUES ('xper_required_target_selection_hold_time',0,1097009302112331,'250');
INSERT INTO SystemVar VALUES ('xper_target_selection_eye_in_time_threshold',0,1097009302112331,'200');
INSERT INTO SystemVar VALUES ('xper_target_selection_eye_out_time_threshold',0,1097009302112331,'100');
INSERT INTO SystemVar VALUES ('xper_target_selection_eye_monitor_start_delay',0,1097009302112331,'200');

INSERT INTO SystemVar VALUES ('xper_choice_target_size',0,1097009302112331,'2.0');
INSERT INTO SystemVar VALUES ('xper_choice_target_distance_from_origin',0,1097009302112331,'5.0');
INSERT INTO SystemVar VALUES ('xper_choice_target_eye_window_size',0,1097009302112331,'3.0');

INSERT INTO SystemVar VALUES ('xper_juice_channel',0,1097009302130475,'0');
-- For analog juice device
INSERT INTO SystemVar VALUES ('xper_juice_channel_min_value',0,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('xper_juice_channel_max_value',0,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('xper_juice_channel_reference',0,1097009302130475,'diff');

INSERT INTO SystemVar VALUES ('xper_juice_delay',0,1097009302130734,'100');
INSERT INTO SystemVar VALUES ('xper_juice_reward_length',0,1097094313592905,'170');
INSERT INTO SystemVar VALUES ('xper_juice_bonus_delay',0,1097009302129752,'100');
INSERT INTO SystemVar VALUES ('xper_juice_bonus_probability',0,1097009302130084,'0.05');

INSERT INTO SystemVar VALUES ('xper_device',0,1097009302131117,'Dev1');

INSERT INTO SystemVar VALUES ('acq_device',0,1097009302131117,'Dev1');
INSERT INTO SystemVar VALUES ('acq_data_chan',0,1097009302131117,'0');
-- Frequency for each channel
INSERT INTO SystemVar VALUES ('acq_master_frequency',0,1097009302131117,'25000');
INSERT INTO SystemVar VALUES ('acq_even_marker_chan',0,1097009302131117,'1');
INSERT INTO SystemVar VALUES ('acq_odd_marker_chan',0,1097009302131117,'2');

INSERT INTO SystemVar VALUES ('acq_device_buffer_size',0,1097009302131117,'25000');
INSERT INTO SystemVar VALUES ('acq_device_buffer_count',0,1097009302131117,'1000');

INSERT INTO SystemVar VALUES ('acq_data_block_size',0,1097009302131117,'10000');

-- List the channels used in acq server
INSERT INTO SystemVar VALUES ('acq_n_channel',0,1097009302131117,'8');
INSERT INTO SystemVar VALUES ('acq_channel',0,1097009302131117,'0');
INSERT INTO SystemVar VALUES ('acq_channel',1,1097009302131117,'1');
INSERT INTO SystemVar VALUES ('acq_channel',2,1097009302131117,'2');
INSERT INTO SystemVar VALUES ('acq_channel',3,1097009302131117,'3');
INSERT INTO SystemVar VALUES ('acq_channel',4,1097009302131117,'4');
INSERT INTO SystemVar VALUES ('acq_channel',5,1097009302131117,'5');
INSERT INTO SystemVar VALUES ('acq_channel',6,1097009302131117,'6');
INSERT INTO SystemVar VALUES ('acq_channel',7,1097009302131117,'7');

INSERT INTO SystemVar VALUES ('acq_channel_type',0,1097009302131117,'half_digital');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',0,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',0,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',0,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',0,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',0,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',0,1097009302131117,'diff');

INSERT INTO SystemVar VALUES ('acq_channel_type',1,1097009302131117,'half_digital');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',1,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',1,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',1,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',1,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',1,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',1,1097009302131117,'diff');

INSERT INTO SystemVar VALUES ('acq_channel_type',2,1097009302131117,'half_digital');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',2,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',2,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',2,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',2,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',2,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',2,1097009302131117,'diff');

INSERT INTO SystemVar VALUES ('acq_channel_type',3,1097009302131117,'analog');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',3,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',3,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',3,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',3,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',3,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',3,1097009302131117,'diff');

INSERT INTO SystemVar VALUES ('acq_channel_type',4,1097009302131117,'analog');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',4,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',4,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',4,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',4,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',4,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',4,1097009302131117,'diff');

INSERT INTO SystemVar VALUES ('acq_channel_type',5,1097009302131117,'analog');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',5,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',5,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',5,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',5,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',5,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',5,1097009302131117,'diff');

INSERT INTO SystemVar VALUES ('acq_channel_type',6,1097009302131117,'analog');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',6,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',6,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',6,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',6,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',6,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',6,1097009302131117,'diff');

INSERT INTO SystemVar VALUES ('acq_channel_type',7,1097009302131117,'analog');
INSERT INTO SystemVar VALUES ('acq_channel_min_value',7,1097009302130475,'-10');
INSERT INTO SystemVar VALUES ('acq_channel_max_value',7,1097009302130475,'10');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v0',7,1097009302131117,'1.0');
INSERT INTO SystemVar VALUES ('acq_channel_digital_v1',7,1097009302131117,'4.0');
INSERT INTO SystemVar VALUES ('acq_channel_frequency',7,1097009302131117,'10000');
INSERT INTO SystemVar VALUES ('acq_channel_reference',7,1097009302131117,'diff');

-- RDS parameters
INSERT INTO SystemVar VALUES ('xper_rds_fixation_point_color',0,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_rds_fixation_point_color',1,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_rds_fixation_point_color',2,1097009302112331,'1');
INSERT INTO SystemVar VALUES ('xper_rds_fixation_point_size',0,1097009302112331,'20');
INSERT INTO SystemVar VALUES ('xper_rds_background_color',0,1097009302112331,'0.2');
INSERT INTO SystemVar VALUES ('xper_rds_background_color',1,1097009302112331,'0.2');
INSERT INTO SystemVar VALUES ('xper_rds_background_color',2,1097009302112331,'0.2');
INSERT INTO SystemVar VALUES ('xper_rds_background_size',0,1097009302112331,'100');


