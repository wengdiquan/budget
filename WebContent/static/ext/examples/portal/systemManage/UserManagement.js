// 用户管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();

	Ext.define('App.UserManagementWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '用户信息',
				width : 350,
				height : 280,
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
					items : [ ],
					buttons : [ '->', {
						id : 'usermanagementwindow-save',
						text : '保存',
						iconCls : 'icon-save',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							if (form.isValid()) {
								window.getEl().mask('数据保存中，请稍候...');
								var vals = form.getValues();
								Ext.Ajax.request({
									url : appBaseUri + '/sys/sysuser/saveSysUser',
									params : {
										cmd : vals['cmd'],
										id : vals['id'],
										userName : vals['userName'],
										password : vals['password'],
										realName : vals['realName'],
										tel : vals['tel'],
										email : vals['email'],
										role : vals['role']
									},
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('usermanagementgrid').getStore().reload();
											} else {
												globalObject.errTip('用户名已存在，请输入唯一值！');
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
						id : 'usermanagementwindow-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, {
						id : 'usermanagementwindow-accept',
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
			App.UserManagementWindow.superclass.constructor.call(this, config);
		}
	});
	
	Ext.define('App.UserManagementWindowQuery', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '用户查询',
				width : 350,
				height : 170,
				bodyPadding : '10 5',
				modal : true,
				layout : 'fit',
				items : [ {
					xtype : 'form',
					fieldDefaults : {
						labelAlign : 'right',
						labelWidth : 90,
						anchor : '95%'
					},
					items : [{
						xtype : 'textfield',
						name : 'name',
						fieldLabel : '用户名',
						emptyText : '请输入用户名'
					},
					{
						xtype : 'textfield',
						name : 'personCode',
						fieldLabel : '人员编码',
						emptyText : '请输入编码'
					}],
					buttons : [ '->',  {
						id : 'usermanagementwindowquery-accept',
						text : '确定',
						iconCls : 'icon-accept',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							var vals = form.getValues();
							var store = Ext.getCmp('usermanagementgrid').getStore();
							
							var extraParams = {
								name:vals['name'],
								personCode:vals['personCode']
							};
							Ext.apply(store.proxy.extraParams, extraParams);
							store.reload();
							window.close();
						}
					}, {
						id : 'usermanagementwindowquery-reset',
						text : '重置',
						iconCls : 'icon-reset',
						width : 80,
						handler : function() {
							this.up('window').down('form').getForm().reset();
						}
					},{
						id : 'usermanagementwindowquery-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, '->' ]
				} ]
			});
			App.UserManagementWindowQuery.superclass.constructor.call(this, config);
		}
	});

	Ext.define('Cscec.app.systemManage.UserManagement', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'center',
		needQuery:true,
		initComponent : function() {
			var me = this;
			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'userId',
					type : 'long'
				}, 'personCode', 'name', 'personType', 'cardNumber', 'gender', 'age',
				'nation', {
					name : 'attendWorkTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				}, {
					name : 'attendWorkTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				}, {
					name : 'enterWorkTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				}, 'political', 'workYears', 'technicalName', {
					name : 'graduationTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				},  'education', 'school', 'major', 'origin', 'enableFlag',
				{
					name : 'lastLoginTime',
					type : 'datetime',
					dateFormat : 'Y-m-d H:i:s'
				}]
			});

			var store = me.createStore({
				modelName : 'ModelList',
				proxyUrl : appBaseUri + '/sys/sysuser/getSysUser',
				proxyDeleteUrl : appBaseUri + '/sys/sysuser/deleteSysUser',
				extraParams : me.extraParams
			});

			var columns = [ {
				text : "ID",
				dataIndex : 'userId',
				width : '5%'
			}, {
				text : "用户名",
				dataIndex : 'name',
				width : '13%'
			}, {
				text : "人员编号",
				dataIndex : 'personCode',
				width : '10%'
			}, {
				text : "人员类别",
				dataIndex : 'personType',
				width : '14%'
			}, {
				text : "身份证号",
				dataIndex : 'cardNumber',
				width : '15%'
			},{
				text : "性别",
				dataIndex : 'gender',
				width : '15%'
			}, {
				text : "年龄",
				dataIndex : 'age',
				width : '15%'
			}, {
				text : "民族",
				dataIndex : 'nation',
				width : '15%'
			}, {
				text : "参加工作时间",
				dataIndex : 'attendWorkTime',
				width : '15%'
			},  {
				text : "进入本公司时间",
				dataIndex : 'enterWorkTime',
				width : '15%'
			},  {
				text : "政治面貌",
				dataIndex : 'political',
				width : '15%'
			},{
				text : "工龄",
				dataIndex : 'workYears',
				width : '15%'
			}, {
				text : "专业技术职务名称",
				dataIndex : 'technicalName',
				width : '15%'
			},{
				text : "毕业时间",
				dataIndex : 'graduationTime',
				width : '15%'
			},{
				text : "最高学历",
				dataIndex : 'education',
				width : '15%'
			},{
				text : "学校",
				dataIndex : 'school',
				width : '15%'
			},{
				text : "专业(中建)",
				dataIndex : 'major',
				width : '15%'
			},{
				text : "籍贯(中建)",
				dataIndex : 'origin',
				width : '15%'
			},{
				text : "最后登录时间",
				dataIndex : 'lastLoginTime',
				width : '19%'
			}, {
				xtype : 'actioncolumn',
				width : '8%',
				items : [ {
					iconCls : 'icon-view',
					tooltip : '查看',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'View'),
					handler : function(grid, rowIndex, colIndex) {
						var gridRecord = grid.getStore().getAt(rowIndex);
						var win = new App.UserManagementWindow({
							hidden : true
						});
						var form = win.down('form').getForm();
						form.loadRecord(gridRecord);
						form.findField('userName').setReadOnly(true);
						form.findField('password').hide();
						form.findField('realName').setReadOnly(true);
						form.findField('tel').setReadOnly(true);
						form.findField('email').setReadOnly(true);
						form.findField('lastLoginTime').show().setReadOnly(true);
						form.findField('roleName').setValue(gridRecord.get('roleName')).setReadOnly(true);
						Ext.getCmp('usermanagementwindow-save').hide();
						Ext.getCmp('usermanagementwindow-cancel').hide();
						Ext.getCmp('usermanagementwindow-accept').show();
						win.show();
					}
				}, {
					iconCls : 'edit',
					tooltip : '修改',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'Edit'),
					handler : function(grid, rowIndex, colIndex) {
						var gridRecord = grid.getStore().getAt(rowIndex);
						var win = new App.UserManagementWindow({
							hidden : true
						});
						win.setHeight(250);
						var form = win.down('form').getForm();
						form.loadRecord(gridRecord);
						form.findField("cmd").setValue("edit");
						form.findField("userName").setReadOnly(true);
						form.findField('password').hide();
						// form.findField('roleName').setRawValue(gridRecord.get('roleName'));
						win.show();
					}
				}, {
					iconCls : 'icon-delete',
					tooltip : '删除',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'Delete'),
					handler : function(grid, rowIndex, colIndex) {
						var entity = grid.getStore().getAt(rowIndex);
						singleId = entity.get('id');
						me.onDeleteClick();
					}
				} ]
			} ];
			
			Ext.apply(this, {
				id : 'usermanagementgrid',
				store : store,
				columns : columns
			});

			store.loadPage(1);
			this.callParent(arguments);
		},
		onAddClick : function() {
			new App.UserManagementWindow().show();
		},
		onViewClick : function() {
			var grid = Ext.getCmp("usermanagementgrid");
			var id = grid.getSelectionModel().getSelection()[0].get('id');
			var gridRecord = grid.getStore().findRecord('id', id);
			var win = new App.UserManagementWindow({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			form.loadRecord(gridRecord);
			form.findField('userName').setReadOnly(true);
			form.findField('password').hide();
			form.findField('realName').setReadOnly(true);
			form.findField('tel').setReadOnly(true);
			form.findField('email').setReadOnly(true);
			form.findField('lastLoginTime').show().setReadOnly(true);
			form.findField('roleName').setValue(grid.getSelectionModel().getSelection()[0].get('roleName')).setReadOnly(true);
			Ext.getCmp('usermanagementwindow-save').hide();
			Ext.getCmp('usermanagementwindow-cancel').hide();
			Ext.getCmp('usermanagementwindow-accept').show();
			win.show();
		},
		onQueryClick:function() {
			var winQuery = new App.UserManagementWindowQuery();
			var form = winQuery.down('form').getForm();
			form.setValues(Ext.getCmp('usermanagementgrid').getStore().proxy.extraParams)
			winQuery.show(); 
		}
	});
});