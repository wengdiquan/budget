//组织管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('App.OrganizationManagementWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '组织管理',
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
					items : [ {
						name : "cmd",
						xtype : "hidden",
						value : 'new'
					}, {
						xtype : 'hiddenfield',
						name : 'organizationId'
					}, {
						xtype : 'textfield',
						name : 'organizationName',
						fieldLabel : '组织名称',
						allowBlank : false
					}, {
						xtype : 'textfield',
						name : 'organizationCode',
						fieldLabel : '组织编码',
						allowBlank : false
					}],
					buttons : [ '->', {
						id : 'organizationmanagementwindow-save',
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
										organizationId : vals['organizationId'],
										organizationName : vals['organizationName']
									},
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('organizationmanagementgrid').getStore().reload();
											} else {
												//globalObject.errTip('用户名已存在，请输入唯一值！');
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
						id : 'organizationmanagementwindow-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, {
						id : 'organizationmanagementwindow-accept',
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
			App.OrganizationManagementWindow.superclass.constructor.call(this, config);
		}
	});
	
	Ext.define('Cscec.app.systemManage.OrganizationManagement', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'center',
		initComponent : function() {
			var me = this;

			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'organizationId',
					type : 'long'
				}, 'organizationCode', 'organizationName', {
					name : 'createTime',
					type : 'datetime',
					dateFormat : 'Y-m-d H:i:s'
				}, {
					name : 'lastUpdateTime',
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
				dataIndex : 'organizationId',
				width : '5%'
			}, {
				text : "组织编码",
				dataIndex : 'organizationCode',
				width : '13%'
			}, {
				text : "组织名称",
				dataIndex : 'organizationName',
				width : '10%'
			}, {
				text : "创建时间",
				dataIndex : 'createTime',
				width : '14%'
			}, {
				text : "修改时间",
				dataIndex : 'lastUpdateTime',
				width : '15%'
			}, {
				xtype : 'actioncolumn',
				width : '8%',
				items : [ {
					iconCls : 'icon-view',
					tooltip : '查看',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'View'),
					handler : function(grid, rowIndex, colIndex) {
						var gridRecord = grid.getStore().getAt(rowIndex);
						var win = new App.OrganizationManagementWindow({
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
				id : 'organizationmanagementgrid',
				store : store,
				columns : columns
			});

			store.loadPage(1);

			this.callParent(arguments);
		},
		onAddClick : function() {
			new App.OrganizationManagementWindow().show();
		},
		onViewClick : function() {
			var grid = Ext.getCmp("organizationmanagementgrid");
			var id = grid.getSelectionModel().getSelection()[0].get('organizationId');
			var gridRecord = grid.getStore().findRecord('organizationId', id);
			var win = new App.OrganizationManagementWindow({
				hidden : true,
				height : 230
			});
			/*var form = win.down('form').getForm();
			form.loadRecord(gridRecord);
			form.findField('userName').setReadOnly(true);
			form.findField('password').hide();
			form.findField('realName').setReadOnly(true);
			form.findField('tel').setReadOnly(true);
			form.findField('email').setReadOnly(true);
			form.findField('lastLoginTime').show().setReadOnly(true);
			form.findField('roleName').setValue(grid.getSelectionModel().getSelection()[0].get('roleName')).setReadOnly(true);
			Ext.getCmp('organizationmanagementwindow-save').hide();
			Ext.getCmp('organizationmanagementwindow-cancel').hide();
			Ext.getCmp('organizationmanagementwindow-accept').show();*/
			//win.show();
		}
	});
});