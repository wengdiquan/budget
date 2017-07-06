Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	// 运材安代码维护
	Ext.define('Budget.app.baseDataManage.YCAManageWin', {
		extend : 'Ext.window.Window',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout: 'border',
				items : [ 
					Ext.create('Budget.app.baseDataManage.YCAManage.YCATypeGrid',{
						id:'ycamanagewin-ycatypegrid'
					}), 
					Ext.create('Budget.app.baseDataManage.YCAManage.YCAValueGrid',{
						id : 'ycamanagewin-ycavaluegrid',
						lookValueField:me.lookValueField,
						yacModelField:me.yacModelField
					}
				)]
			});
			this.callParent(arguments);
		}
	});
	
	//费用大类
	Ext.define('Budget.app.baseDataManage.YCAManage.YCATypeGrid', {
		extend : 'Ext.tree.Panel',
		region : 'west',
		width : '20%',
		border : true,
		title:"大类",
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var ycaTypeStore = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/yca/queryTreeList',
					extraParams : {listCode:"COST_CODE"},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			Ext.apply(this, {
				rootVisible : false,
				store : ycaTypeStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						
						me.lookValueId = record.data.id;
						if(record.data.depth == 1){
							Ext.getCmp('ycamanagewin-ycavaluegrid').getStore().load({
								params : {
									'lookTypeId' : me.lookValueId,
									"param2":"onelevel"
								}
							});
						}
						if(record.data.depth == 2){
							Ext.getCmp('ycamanagewin-ycavaluegrid').getStore().load({
								params : {
									'lookValueId' : me.lookValueId,
									"param2":""
								}
							});
						}
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.baseDataManage.YCAManage.YCAValueGrid', {
		extend : 'Ext.grid.Panel',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('YCAValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id_X',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'code', 'name', 'unit', 'category','type',
				{
					name : 'noPrice',
					type : 'double'
				},
				{
					name : 'price',
					type : 'double'
				},
				{
					name : 'rate',
					type : 'double'
				}]
			});
			
			var ycaValueStore = Ext.create('Ext.data.Store', {
				model : 'YCAValueList',
				autoLoad : false,
				remoteSort : false,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/yca/queryYCAInfo',
					extraParams : {"lookValueId": Ext.getCmp('ycamanagewin-ycatypegrid').lookValueId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var ycaValueColumns = [ 
			{
					text:'', width:25,xtype:'rownumberer'
			},{
				text : "编码",
				dataIndex : 'code',
				width : '14%',
				hidden : true
			},{
				text : "编码",
				dataIndex : 'code',
				width : '14%',
				hidden : false,
				align:'right',
				renderer:function(v){
					return v.substring(v.indexOf('-') +1 );
				}
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '20%'
			},{
				text : "类别",
				dataIndex : 'type',
				sortable : false,
				width : '8%',
				align:"center"
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				align:'center',
				width : '8%'
			},{
				text : "不含税单价",
				dataIndex : 'noPrice',
				sortable : false,
				width : '12%',
				align:'right'
			},{
				text : "含税单价",
				dataIndex : 'price',
				sortable : false,
				width : '12%',
				align:'right'
			},{
				text : "税率",
				dataIndex : 'rate',
				sortable : false,
				width : '12%',
				align:'right'
			},{
				text : "种类",
				dataIndex : 'category',
				sortable : false,
				hidden:true
			},{
				text : "looktype_id",
				dataIndex : 'looktype_id',
				hidden : true
			}];
			Ext.apply(this, {
				store : ycaValueStore,
				selModel : Ext.create('Ext.selection.CheckboxModel', {mode:'single'}),
				columns : ycaValueColumns,
				bbar : Ext.create('Ext.PagingToolbar', {
					store : ycaValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:true,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			ycaValueStore.on('beforeload',function(){
				if(Ext.getCmp('ycamanagewin-ycatypegrid').getSelectionModel().getSelection()[0].data.depth == 1){
					Ext.apply(ycaValueStore.proxy.extraParams, {"lookTypeId": Ext.getCmp('ycamanagewin-ycatypegrid').lookValueId,"param2":"onelevel"});
				}else if(Ext.getCmp('ycamanagewin-ycatypegrid').getSelectionModel().getSelection()[0].data.depth == 2){
					Ext.apply(ycaValueStore.proxy.extraParams, {"lookValueId": Ext.getCmp('ycamanagewin-ycatypegrid').lookValueId,"param2":""});
				}
			});

			this.on('itemdblclick', function(_this, record, item, idx, e, eOpts){
				if(me.lookValueField != undefined){
					me.lookValueField.setValue(record.get('name'));
					me.yacModelField.setValue(record.get('id'));
					me.up('window').close();
				}
			});
			
			this.callParent(arguments);
		}
	});
});