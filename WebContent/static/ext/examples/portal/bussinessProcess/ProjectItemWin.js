/*单位工程子目选择*/
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin', {
		extend : 'Ext.window.Window',
		title:"查询",
		bitItemGrid:"",
		initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				width : "70%",
				height : "70%",
				bodyPadding : '5 5',
				modal : true,
				layout : 'fit',
				items : [Ext.create("Budget.app.bussinessProcess.ProjectItemWin.TabPanel", {
						bitProjectId: me.bitProjectId,
						bitItemGrid: me.bitItemGrid,
						showDing:me.showDing
					})
				]
  			});
  			this.callParent(arguments);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin.TabPanel', {
		extend : 'Ext.tab.Panel',
        initComponent : function() {
          	var me = this;
          	
  			Ext.apply(this, {
  				activeTab: me.showDing ? 0 : 1 ,
  				items:[{  
                    title: '定额',  
                    layout : 'border',
                    hidden:!me.showDing,
                    items:[
                    	 Ext.create('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CMGrid',{
                    		 bitItemGrid:me.bitItemGrid,
                    		 bitProjectId: me.bitProjectId
                    	 }),
                    	 Ext.create('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CMDetailGrid', {
                    		 bitItemGrid:me.bitItemGrid,
                    		 bitProjectId: me.bitProjectId
                    	 })
                    ],
                    itemId: 'dinge'  
                },{  
                    title: '运材安', 
                    layout : 'border',
                    items:[
                    	Ext.create('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CostTypeTree',{
                    		 bitItemGrid:me.bitItemGrid,
                    		 bitProjectId: me.bitProjectId
                    	}),
                    	Ext.create('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CostValueGrid',{
                    		 bitItemGrid:me.bitItemGrid,
                    		 bitProjectId: me.bitProjectId
                    	})
                    ],
                    itemId: 'yca'
                }]
  			});
  			
			this.callParent(arguments);
		}
	});
	
	//模块名称tree
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CMGrid', {
		extend : 'Ext.tree.Panel',
		id:"projectbitwin-tabpanel-cmgrid",
		region : 'west',
		width : '35%',
		border : true,
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var costTypeStore = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/categorymodel/getCategoryModelList',
					extraParams : {listCode:"COST_CODE"},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			Ext.apply(this, {
				rootVisible : false,
				store : costTypeStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						
						me.parentId = record.data.id;
						
						Ext.getCmp('projectbitwin-tabpanel-cmdetailgrid').getStore().load({
							params : {
								'parentId' : me.parentId
							}
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CMDetailGrid', {
		extend : 'Ext.grid.Panel',
		plain : true,
		region : 'center',
		id:'projectbitwin-tabpanel-cmdetailgrid',
		border:true,
		initComponent : function() {
			var me = this;
			
			var bitItemGrid = me.bitItemGrid;
			
			Ext.define('CostValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'looktype_id',
					type : 'int'
				}, 'code', 'name', 'unit', 'price','transportFee','materialFee','installationFee',]
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'CostValueList',
				autoLoad : false,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/categorymodel/queryDetailCategoryModel',
					extraParams : {"parentId": Ext.getCmp('projectbitwin-tabpanel-cmgrid').parentId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [{
				text:'', width:25,xtype:'rownumberer'
			},{
				text : "编码",
				dataIndex : 'code',
				width : '15%',
				hidden : false
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '40%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '20%'
			},{
				text : "单价",
				dataIndex : 'price',
				sortable : false,
				width : '20%'
			},{
				text : "运输费",
				dataIndex : 'transportFee',
				sortable : false,
				width : '10%',
				hidden : true
			},{
				text : "材料费",
				dataIndex : 'materialFee',
				sortable : false,
				hidden : true,
				width : '10%'
			},{
				text : "安装费",
				dataIndex : 'installationFee',
				sortable : false,
				hidden : true,
				width : '10%'
			},{
				text : "id",
				dataIndex : 'id',
				hidden : true
			}];
			var times = 1;
			Ext.apply(this, {
				store : costValueStore,
				listeners : {
					'itemdblclick' : function(item, record) {
						var bitRecord =  bitItemGrid.getSelectionModel().getSelection()[0];
						//修改
						/*
						if(bitRecord.get("id") == null || bitRecord.get("id")  == ""){
							bitRecord.set('code', record.get('code'));
							bitRecord.set('type', '定');
							bitRecord.set('name', record.get('name'));
							bitRecord.set('unit', record.get('unit'));
							bitRecord.set('amount', null);
							bitRecord.set('dtgcl', 1);
							bitRecord.set('singlePrice', 1);
							bitRecord.set('price', 1);
							bitRecord.set('sumPrice', 1);
							bitRecord.set('remark', null);
						}else{
							//数据对象
							var newRecord = {
								code: record.get('code'),
								type: '定',
								name: record.get('name'),
								unit: record.get('unit'),
								amount:null,
								dtgcl:1,
								singlePrice:1,
								price:1,
								sumPrice:1,
								remark:null
							};
							bitItemGrid.getStore().add(newRecord);
						}*/
						
						//插入数据库
						Ext.getCmp('projectbitwin-tabpanel-cmdetailgrid').getEl().mask('数据处理中，请稍候...');
						Ext.Ajax.request({
							 url : appBaseUri + '/unitproject/insertItem', //新增单位工程的子目
							 params : {
								 type:"DING",
								 dingId:record.get("id"),
								 bitProjectId: me.bitProjectId,
								 unitProjectId:bitRecord.get("id"),
								 unitProjectCode:bitRecord.get("code"),
								 times:times
							 },
							 method : "POST",
							 success : function(response) {
								 if (response.responseText != '') {
									 var res = Ext.JSON.decode(response.responseText);
									 if (res.success) {
										 times++;
										 bitItemGrid.getStore().reload({
											callback:function(){
												bitItemGrid.getSelectionModel().select(bitRecord.data.index + 1);
										 	}
										 });
										
									 } else {
										 globalObject.errTip(res.msg);
									 }
									 
									 Ext.getCmp('projectbitwin-tabpanel-cmdetailgrid').getEl().unmask();
								 }
							 },
							 failure : function(response) {
								 globalObject.errTip('操作失败！');
							 }
						 });
					}
				},
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : costValueColumns,
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
			this.callParent(arguments);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CostTypeTree', {
		extend : 'Ext.tree.Panel',
		id : 'projectbitwin-tabpanel-costtypetree',
		region : 'west',
		width : '18%',
		border : true,
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var costTypeStore = Ext.create('Ext.data.TreeStore', {
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
				store : costTypeStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						me.lookValueId = record.data.id;
						var param = {};
						if(record.data.depth == 1){
							param.lookTypeId = record.data.id;
						}else{
							param.lookValueId = record.data.id;
						}
						Ext.getCmp('projectbitwin-tabpanel-costvaluegrid').getStore().load({
							params : param
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});
	
	// 详细值
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CostValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'projectbitwin-tabpanel-costvaluegrid',
		plain : true,
		region : 'center',
		border:true,
		initComponent : function() {
			var me = this;
			
			var bitItemGrid = me.bitItemGrid;
			
			Ext.define('YCAGridList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'looktype_id',
					type : 'int'
				}, 'code', 'name', 'unit', 'content','amount','noPrice','price','rate','sumNoPrice','sumPrice','lookValueId']
			});
			
			var YCAGridStore = Ext.create('Ext.data.Store', {
				model : 'YCAGridList',
				autoLoad : false,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/categorymodel/queryDetailYCA',
					extraParams : {"lookValueId": Ext.getCmp('projectbitwin-tabpanel-costtypetree').lookValueId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "编号",
				dataIndex : 'code',
				width : '20%',
				hidden : false
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '20%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '7%',
				align:'center'
			},{
				text : "含量",
				dataIndex : 'content',
				hidden : true
			},{
				text : "数量",
				dataIndex : 'amount',
				hidden : true
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
				width : '11%',
				align:'right'
			},{
				text : "税率",
				dataIndex : 'rate',
				hidden : true
			},{
				text : "不含税合价",
				dataIndex : 'sumNoPrice',
				sortable : false,
				width : '12%',
				align:'right'
			},{
				text : "含税合价",
				dataIndex : 'sumPrice',
				hidden : true
			},{
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text : "lookValueId",
				dataIndex : 'lookValueId',
				hidden : true
			}];
			var times = 1;
			Ext.apply(this, {
				store : YCAGridStore,
				listeners : {
					'itemdblclick' : function(item, record) {
						var bitRecord =  bitItemGrid.getSelectionModel().getSelection()[0];
						
						//插入数据库
						Ext.getCmp('projectbitwin-tabpanel-costvaluegrid').getEl().mask('数据处理中，请稍候...');
						Ext.Ajax.request({
							 url : appBaseUri + '/unitproject/insertItem', //新增单位工程的子目
							 params : {
								 type:"YCA",
								 ycaId:record.get("id"),
								 bitProjectId: me.bitProjectId,
								 unitProjectId:bitRecord.get("id"),
								 unitProjectCode:bitRecord.get("code"),
								 times:times
							 },
							 method : "POST",
							 success : function(response) {
								 if (response.responseText != '') {
									 var res = Ext.JSON.decode(response.responseText);
									 if (res.success) {
										 times++;
										 bitItemGrid.getStore().reload({
											callback:function(){
												bitItemGrid.getSelectionModel().select(bitRecord.data.index + 1);
										 	}
										 });
										
									 } else {
										 globalObject.errTip(res.msg);
									 }
									 
									 Ext.getCmp('projectbitwin-tabpanel-costvaluegrid').getEl().unmask();
								 }
							 },
							 failure : function(response) {
								 globalObject.errTip('操作失败！');
							 }
						 });
					}
				},
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : costValueColumns,
				bbar : Ext.create('Ext.PagingToolbar', {
					store : YCAGridStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
			this.callParent(arguments);
		}
	});
	/*Ext.define('Budget.app.bussinessProcess.ProjectItemWin.TabPanel.CostValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'projectbitwin-tabpanel-costvaluegrid',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;

			var bitItemGrid = me.bitItemGrid;
			
			Ext.define('CostValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'looktype_id',
					type : 'int'
				}, 'code', 'name', 'unit', 'category']
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'CostValueList',
				autoLoad : false,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/yca/queryYCAInfo',
					extraParams : {"lookValueId": Ext.getCmp('projectbitwin-tabpanel-costtypetree').lookValueId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "费用代码",
				dataIndex : 'code',
				width : '14%',
				hidden : false
			},{
				text : "费用名称",
				dataIndex : 'name',
				sortable : false,
				width : '30%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '14%'
			}];
			Ext.apply(this, {
				store : costValueStore,
				listeners : {
					'itemdblclick' : function(item, record) {
						
						var bitRecord =  bitItemGrid.getSelectionModel().getSelection()[0];
						var itmes = 1;
						//修改
						if(bitRecord.get("code") == null || bitRecord.get("code")  == ""){
								
							bitRecord.set('code', record.get('code'));
							bitRecord.set('type', '定');
							bitRecord.set('name', record.get('name'));
							bitRecord.set('unit', record.get('unit'));
							bitRecord.set('amount', null);
							bitRecord.set('dtgcl', 1);
							bitRecord.set('singlePrice', 1);
							bitRecord.set('price', 1);
							bitRecord.set('sumPrice', 1);
							bitRecord.set('remark', null);
						}else{
							//数据对象
							var newRecord = {
									code: record.get('code'),
									type: '定',
									name: record.get('name'),
									unit: record.get('unit'),
									amount:null,
									dtgcl:1,
									singlePrice:1,
									price:1,
									sumPrice:1,
									remark:null
							};
							bitItemGrid.getStore().add(newRecord);
						}
					}
				},
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : costValueColumns,
				bbar : Ext.create('Ext.PagingToolbar', {
					store : costValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
			this.callParent(arguments);
		}
	});*/
});