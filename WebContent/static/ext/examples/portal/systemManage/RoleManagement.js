// 角色管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Cscec.app.systemManage.RoleManagement', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				items : [ Ext.create('Cscec.app.systemManage.RoleManagement.roleGrid', {
					cButtons : me.cButtons,
					cName : me.cName
				}), Ext.create('Cscec.app.util.UserWindow',{
					title:"关联用户",
					id:"userwindowidforrole",
					needAutoLoad:false
				})]
			});
			
			var usergrid = Ext.getCmp("userwindowidforrole");
			usergrid.getStore().addListener("beforeload", function(store,records){
				var grid = Ext.getCmp("rolemanagementgrid");
				var roleId = grid.getSelectionModel().getSelection()[0].get('roleId');
				var new_params = {
					roleId:roleId,
				};
				Ext.apply(store.proxy.extraParams, new_params);
			});
			
			this.callParent(arguments);
		}
	});
	
	Ext.define('App.RoleManagementWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '角色信息',
				width : 350,
				height : 150,
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
						name : 'roleId'
					}, {
						xtype : 'textfield',
						name : 'roleName',
						fieldLabel : '角色名称',
						emptyText : '请输入角色名称',
						allowBlank : false
					}],
					buttons : [ '->', {
						id : 'rolemanagementwindow-save',
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
									url : appBaseUri + '/sys/role/saveRole',
									params : {
										cmd : vals['cmd'],
										id : vals['roleId'],
										roleName : vals['roleName']
									},
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												window.close();
												globalObject.msgTip('操作成功！');
												Ext.getCmp('rolemanagementgrid').getStore().reload();
											} else {
												globalObject.errTip('角色名称已经存在，请重新输入！');
											}
										}
									},
									failure : function(response) {
										globalObject.errTip('操作失败！');
									}
								});
								window.getEl().unmask();
							}
						}
					}, {
						id : 'rolemanagementwindow-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, {
						id : 'rolemanagementwindow-accept',
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
			App.RoleManagementWindow.superclass.constructor.call(this, config);
		}
	});
	
	Ext.define('Cscec.app.systemManage.RoleManagement.roleGrid', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'north',
		height : '40%',
		border:true,
		title:'角色',
		initComponent : function() {
			var me = this;
			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'roleId',
				fields : [ {
					name : 'roleId',
					type : 'long'
				}, 'roleName', 'enableFlag' ]
			});

			var store = me.createStore({
				modelName : 'ModelList',
				proxyUrl : appBaseUri + '/sys/role/getRole',
				proxyDeleteUrl : appBaseUri + '/sys/role/deleteRole',
				extraParams : me.extraParams
			});

			var columns = [ {
				text : "ID",
				xtype : "hidden",
				dataIndex : 'roleId'
			}, {
				text : "角色名称",
				dataIndex : 'roleName',
				width:200,
				sortable:false
			},  {
				text : "是否有效",
				dataIndex : 'enableFlag',
				align:"center",
				width:100,
				sortable:false,
				renderer:rendenerYN
			}];
				
			Ext.apply(this, {
				id : 'rolemanagementgrid',
				store : store,
				columns : columns
			});

			store.loadPage(1);
			this.callParent(arguments);
			
			me.addListener("itemclick", function(item, record){
				me.onViewClick();
			});
		},
		onAddClick : function() {
			new App.RoleManagementWindow().show();
		},
		onViewClick : function() {
			var usergrid = Ext.getCmp("userwindowidforrole");
			usergrid.getStore().loadPage(1);
		}
	});
	
	//角色人员信息
	Ext.define('Cscec.app.systemManage.RoleManagement.userGrid', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'south',
		height : '60%',
		border:true,
		title:'关联用户',
		initComponent : function() {
			var me = this;
			Ext.define('UserList', {
				extend : 'Ext.data.Model',
				idProperty : 'userId',
				fields : [ {
					name : 'userId',
					type : 'long'
				}, 'userName', 'enableFlag' ]
			});

			var store = me.createStore({
				modelName : 'UserList',
				proxyUrl : appBaseUri + '/sys/role/getRole',
				proxyDeleteUrl : appBaseUri + '/sys/role/deleteRole',
				extraParams : me.extraParams
			});

			var columns = [ {
				text : "ID",
				xtype : "hidden",
				dataIndex : 'roleId'
			}, {
				text : "用户名",
				dataIndex : 'roleName',
				width:200,
				sortable:false
			},  {
				text : "",
				dataIndex : 'enableFlag',
				align:"center",
				width:100,
				sortable:false
			}];

			Ext.apply(this, {
				id : 'usermanagementgrid',
				store : store,
				columns : columns
			});

			store.loadPage(1);

			this.callParent(arguments);
		},
		onAddClick : function() {
			new App.RoleManagementWindow().show();
		},
		onViewClick : function() {
			/*var grid = Ext.getCmp("rolemanagementgrid");
			var id = grid.getSelectionModel().getSelection()[0].get('id');
			var gridRecord = grid.getStore().findRecord('id', id);
			var win = new App.RoleManagementWindow({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			form.findField('name').setReadOnly(true);
			form.findField('remark').setReadOnly(true);
			form.loadRecord(gridRecord);
			Ext.getCmp('rolemanagementwindow-save').hide();
			Ext.getCmp('rolemanagementwindow-cancel').hide();
			Ext.getCmp('rolemanagementwindow-accept').show();
			win.show();*/
		}
	});
});