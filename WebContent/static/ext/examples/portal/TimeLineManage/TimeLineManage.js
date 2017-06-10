//时间轴
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Cscec.app.TimeLineManage.TimeLineManageWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '项目时间轴设置',
				width : 350,
				height : 220,
				bodyPadding : '10 5',
				modal : true,
				layout : 'fit',
				items : [ {
					xtype : 'form',
					fieldDefaults : {
						labelAlign : 'left',
						labelWidth : 90,
						anchor : '100%'
					},
					items : [ {                  //定义隐藏参数
						name : "cmd",
						xtype : "hidden",
						value : 'new'
					}, {
						xtype : 'hiddenfield',
						name : 'timeLineId'
					}, {
						xtype : 'textfield',
						name : 'timeLineName',
						fieldLabel : '项目时间轴名称',
						allowBlank : false
					},{	    
						xtype : 'datefield',
						name : 'timeLineStart',
						fieldLabel : '项目开始时间',
						format : 'Y-m-d',
						// maxValue : new Date(),
						// value : new Date(),
						editable:false,
						allowBlank : true
					},{
						xtype : 'datefield',
						name : 'timeLineEnd',
						fieldLabel : '项目结束时间',
						format : 'Y-m-d',
						// maxValue : new Date(),
						// value : new Date(),
						editable:false,
						allowBlank : true
					}],
					buttons : [ '->', {          //定义保存
						id : 'TimeLineManage-save',
						text : '保存',
						iconCls : 'icon-save',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							
							if (form.isValid()) {
								var vals = form.getValues();
//								if (isNaN(vals['sortOrder'])) {
//									globalObject.errTip('排序项只能输入数字！');
//									return;
//								}
								window.getEl().mask('数据保存中，请稍候...');
								var sb = new StringBuffer();
								Ext.Ajax.request({
									url : appBaseUri + '/sys/timeline/savetimeline',
									params : vals/*{   //这里设置保存所有提交合同字段
										cmd : vals['cmd'],
										timeLineId : vals['timeLineId'],
										timeLineName : vals['timeLineName'],
										timeLineStart : vals['timeLineStart'],	
								        timeLineEnd : vals['timeLineEnd']
									}*/,
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('timelinemanagegrid').getStore().reload();
											} else {
												//globalObject.msgTip('菜单代号不能重复！');
											}
										}
									},
									failure : function(response) {
										globalObject.errTip('操作失败！');
									}
								});
								window.getEl().unmask();
								window.close();
							}
						}
					}, {
						id : 'TimeLineManage-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, {
						id : 'TimeLineManage-accept',
						text : '确定',
						hidden : true,
						iconCls : 'icon-accept',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, '->' ]
				} ]
			});
			Cscec.app.TimeLineManage.TimeLineManageWindow.superclass.constructor.call(this, config);
		}
	});

	//这是啥?????????????????????????????????????????????????????????????????????????????????????????????????????????
	Ext.define('Cscec.app.TimeLineManage.TimeLineManage', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'center',
		idProperty:"timeLineId",
		initComponent : function() {
			var me = this;

			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'timeLineId',
				fields : [ {
					name : 'timeLineId',
					type : 'long'
				},'timeLineName','timeLineStart','timeLineEnd']
			});

			//显示列表
			var store = me.createStore({
				modelName : 'ModelList',
				proxyUrl : appBaseUri + '/sys/timeline/timelinelist',
				proxyDeleteUrl : appBaseUri + '/sys/timeline/deletetimeline',
				extraParams : me.extraParams,
				sortProperty : 'timeLineId',
				sortDirection : 'ASC'
			});

			var columns = [ {
				text : "ID",
				dataIndex : 'timeLineId',
				width : '3%',
				sortable : false
			}, {
				text : "项目时间轴名称",
				dataIndex : 'timeLineName',
				width : '14%',
				sortable : false
			},  {
				text : "项目开始时间",
				dataIndex : 'timeLineStart',
				width : '14%',
				align:"center",
				sortable : false
			},{
				text : "项目结束时间",
				dataIndex : 'timeLineEnd',
				width : '14%',
				align:"center",
				sortable : false
			},{
				text : "节点设置",
				dataIndex : '',
				width : '14%',
				sortable : false,
				align:"center",
				renderer:function(value, metaV, record){
					return "<a href='javascript:;' onclick='window.openNodeWin(" + record.get('timeLineId')+ ")' style='text-decoration:none'>节点设置</a>"
				}
			}];
			//这是实现父类的方法
			Ext.apply(this, {
				id : 'timelinemanagegrid',
				store : store,
				columns : columns
			});

			store.loadPage(1);
			this.callParent(arguments);
		},
		onAddClick : function() {
			new Cscec.app.TimeLineManage.TimeLineManageWindow().show();
		},
		onEditClick : function(){
			var grid = Ext.getCmp("timelinemanagegrid");
			var id = grid.getSelectionModel().getSelection()[0].get('timeLineId');
			var gridRecord = grid.getStore().findRecord('timeLineId', id);
			var win = new Cscec.app.TimeLineManage.TimeLineManageWindow({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			form.loadRecord(gridRecord);
			form.findField("cmd").setValue("edit");
			Ext.getCmp('TimeLineManage-accept').hide();
			Ext.getCmp('TimeLineManage-save').show();
			Ext.getCmp('TimeLineManage-cancel').show();
			win.show();
		},
		onViewClick : function() {
			var grid = Ext.getCmp("timelinemanagegrid");
			var id = grid.getSelectionModel().getSelection()[0].get('timeLineId');
			var gridRecord = grid.getStore().findRecord('timeLineId', id);
			var win = new Cscec.app.TimeLineManage.TimeLineManageWindow({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			form.loadRecord(gridRecord);
			Ext.getCmp('TimeLineManage-save').hide();
			Ext.getCmp('TimeLineManage-cancel').hide();
			Ext.getCmp('TimeLineManage-accept').show();
			win.show();
		}
	});
	//打开NodeList
	function openNodeWin(_timeLineId){
		globalObject.openWindow('时间节点', 'TimeLineManage.TimeNodeGrid', '80%', {height:400, _timeLineId:_timeLineId});
	}
	window.openNodeWin = openNodeWin;
});
