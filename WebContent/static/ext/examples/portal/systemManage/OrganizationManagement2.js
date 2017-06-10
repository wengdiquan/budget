// 组织管理2（菜单管理）
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();

	Ext.define('App.OrganizationManagementWindow2', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '组织管理',
				width : 350,
				height : 440,
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
					}],
					buttons : [ '->', {
						id : 'organizationmanagementwindow2-save',
						text : '保存',
						iconCls : 'icon-save',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							if (form.isValid()) {
								var vals = form.getValues();
								if (isNaN(vals['sortOrder'])) {
									globalObject.errTip('排序项只能输入数字！');
									return;
								}
								window.getEl().mask('数据保存中，请稍候...');
								var sb = new StringBuffer();
								Ext.Ajax.request({
									url : appBaseUri + '/sys/organization/saveOrganization',
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
						id : 'organizationmanagementwindow2-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, {
						id : 'organizationmanagementwindow2-accept',
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
			App.OrganizationManagementWindow2.superclass.constructor.call(this, config);
		}
	});

	Ext.define('Cscec.app.systemManage.OrganizationManagement2', {
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
				}, 'organizationName']
			});

			var store = me.createStore({
				modelName : 'ModelList',
				proxyUrl : appBaseUri + '/sys/organization/orglist',
				proxyDeleteUrl : appBaseUri + '/sys/authority/deleteAuthority',
				extraParams : me.extraParams,
				sortProperty : 'organizationId',
				sortDirection : 'ASC'
			});

			var columns = [ {
				text : "ID",
				dataIndex : 'organizationId',
				width : '3%',
				sortable : false
			}, {
				text : "组织名称",
				dataIndex : 'organizationName',
				width : '14%',
				sortable : false
			}, {
				xtype : 'actioncolumn',
				width : '8%',
				items : [ {
					iconCls : 'icon-view',
					tooltip : '查看',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'View'),
					handler : function(grid, rowIndex, colIndex) {
						var gridRecord = grid.getStore().getAt(rowIndex);
						var win = new App.OrganizationManagementWindow2({
							hidden : true
						});
						var form = win.down('form').getForm();
						Ext.getCmp('resourcemanagementwindow2-save').hide();
						Ext.getCmp('resourcemanagementwindow2-cancel').hide();
						Ext.getCmp('resourcemanagementwindow2-accept').show();
						win.show();
					}
				}, {
					iconCls : 'edit',
					tooltip : '修改',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'Edit'),
					handler : function(grid, rowIndex, colIndex) {
						var gridRecord = grid.getStore().getAt(rowIndex);
						var win = new App.OrganizationManagementWindow2({
							hidden : true
						});
						var form = win.down('form').getForm();
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
				id : 'organizationmanagementgrid2',
				store : store,
				columns : columns
			});

			store.loadPage(1);

			this.callParent(arguments);
		},
		onAddClick : function() {
			new App.OrganizationManagementWindow2().show();
		},
		onViewClick : function() {
			var grid = Ext.getCmp("organizationmanagementgrid2");
			var id = grid.getSelectionModel().getSelection()[0].get('id');
			var gridRecord = grid.getStore().findRecord('id', id);
			var win = new App.OrganizationManagementWindow2({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			Ext.getCmp('resourcemanagementwindow2-save').hide();
			Ext.getCmp('resourcemanagementwindow2-cancel').hide();
			Ext.getCmp('resourcemanagementwindow2-accept').show();
			win.show();
		}
	});
});