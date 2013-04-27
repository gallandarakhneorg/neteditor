#!/usr/bin/gmlviewer
Version "2"
Creator "org.arakhne.neteditor.io.gml.GMLWriter 16.1-SNAPSHOT"
graph [
  directed "1"
  type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.constructs.FiniteStateMachine"
  uuid "52524334-71b1-4d6c-a4b9-e0bc66ffbc86"
  attributes [
  ]
  node [
    id 0
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.constructs.FSMState"
    uuid "19b37fa4-2a0c-4919-a946-fd1fc7638f29"
    attributes [
      name "1"
    ]
    edgeAnchor [
      id 1
      type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.constructs.FSMAnchor"
      uuid "e094bde1-4618-4371-be1b-b7713a2868d7"
      attributes [
        location [
          type "enum"
          name "org.arakhne.neteditor.formalism.AnchorLocation"
          value "CENTER"
        ]
      ]
    ]
  ]
  node [
    id 2
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.constructs.FSMState"
    uuid "7c323e30-f3b6-420d-9fbe-c026a2ad6203"
    attributes [
      name "2"
    ]
    edgeAnchor [
      id 3
      type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.constructs.FSMAnchor"
      uuid "7b2e7ca5-fdf9-4529-ad01-8254d81d5efc"
      attributes [
        location [
          type "enum"
          name "org.arakhne.neteditor.formalism.AnchorLocation"
          value "CENTER"
        ]
      ]
    ]
  ]
  edge [
    id 4
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.constructs.FSMTransition"
    uuid "c91d38c1-8e9b-4cbb-be93-391286697ef7"
    attributes [
      guard "condition"
    ]
    source 0
    target 2
    sourcePort 1
    targetPort 3
  ]
]
graphics [
  Figure [
    viewid "7207b0f8-3e55-4da2-a016-f919b3086320"
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fig.figure.decoration.EllipseFigure"
    uuid "46eafa97-22da-400b-b882-d765c3ee84e4"
    attributes [
      filled [
        type "boolean"
        value 1
      ]
      framed [
        type "boolean"
        value 1
      ]
      height 84.0
      isAutoLockAssociatedFigures [
        type "boolean"
        value 1
      ]
      islocked [
        type "boolean"
        value 0
      ]
      isselectable [
        type "boolean"
        value 1
      ]
      maxheight 3.4028234663852886E38
      maxwidth 3.4028234663852886E38
      minheight 40.0
      minwidth 40.0
      resizedirections [
        type "set"
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_EAST"
        ]
      ]
      uuid [
        type "uuid"
        value "46eafa97-22da-400b-b882-d765c3ee84e4"
      ]
      viewuuid [
        type "uuid"
        value "7207b0f8-3e55-4da2-a016-f919b3086320"
      ]
      width 82.0
      x 285.5
      y 132.0
    ]
  ]
  Figure [
    viewid "7207b0f8-3e55-4da2-a016-f919b3086320"
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fig.figure.decoration.BitmapFigure"
    uuid "52a81342-9128-4a65-b06e-94f9016d11d9"
    attributes [
      filename [
        type "url"
        value "r0.png"
      ]
      framed [
        type "boolean"
        value 1
      ]
      height 51.0
      isAutoLockAssociatedFigures [
        type "boolean"
        value 1
      ]
      islocked [
        type "boolean"
        value 0
      ]
      isselectable [
        type "boolean"
        value 1
      ]
      maxheight 3.4028234663852886E38
      maxwidth 3.4028234663852886E38
      minheight 40.0
      minwidth 40.0
      resizedirections [
        type "set"
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_EAST"
        ]
      ]
      uuid [
        type "uuid"
        value "52a81342-9128-4a65-b06e-94f9016d11d9"
      ]
      viewuuid [
        type "uuid"
        value "7207b0f8-3e55-4da2-a016-f919b3086320"
      ]
      width 105.0
      x 373.5
      y 232.0
    ]
  ]
  Figure [
    viewid "7207b0f8-3e55-4da2-a016-f919b3086320"
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.figures.FSMTransitionFigure"
    uuid "5a4639d9-087e-45fc-9217-8fcc35ceea5f"
    modelid "4"
    attributes [
      controlpoints "(275.39798|221.48)(322.5|185.0)(480.5|169.02248)"
      drawingmethod [
        type "enum"
        name "org.arakhne.neteditor.fig.view.DrawingMethod"
        value "SEGMENTS"
      ]
      height 56.448455810546875
      isAutoLockAssociatedFigures [
        type "boolean"
        value 1
      ]
      islocked [
        type "boolean"
        value 0
      ]
      isselectable [
        type "boolean"
        value 1
      ]
      maxheight 3.4028234663852886E38
      maxwidth 3.4028234663852886E38
      minheight 0.0
      minwidth 0.0
      resizedirections [
        type "set"
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_EAST"
        ]
      ]
      uuid [
        type "uuid"
        value "5a4639d9-087e-45fc-9217-8fcc35ceea5f"
      ]
      viewuuid [
        type "uuid"
        value "7207b0f8-3e55-4da2-a016-f919b3086320"
      ]
      width 205.10202026367188
      x 275.3979797363281
      y 165.0315399169922
      endsymbol [
        type "ns"
        value [
          angle 3.040806531906128
          invert [
            type "boolean"
            value 0
          ]
          isfilled [
            type "boolean"
            value 1
          ]
          length 10.0
          type "org.arakhne.neteditor.fig.figure.edge.symbol.TriangleEdgeSymbol"
          x 480.0
          y 169.0
        ]
      ]
    ]
    Figure [
      coercionid "majorLabel"
      type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fig.figure.coercion.CoercedTextFigure"
      uuid "d045d5af-b7fa-46a7-9225-01785deed89d"
      attributes [
        anchorDescription 0.5
        anchorX 390.3814392089844
        anchorY 178.13558959960938
        dx -14.0059814453125
        dy -6.8085479736328125
        filled [
          type "boolean"
          value 0
        ]
        framed [
          type "boolean"
          value 0
        ]
        height 13.96875
        isAutoLockAssociatedFigures [
          type "boolean"
          value 1
        ]
        islocked [
          type "boolean"
          value 0
        ]
        isselectable [
          type "boolean"
          value 1
        ]
        maxheight 3.4028234663852886E38
        maxwidth 3.4028234663852886E38
        minheight 1.0
        minwidth 1.0
        resizedirections [
          type "set"
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "NORTH_WEST"
          ]
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "NORTH"
          ]
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "NORTH_EAST"
          ]
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "WEST"
          ]
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "EAST"
          ]
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "SOUTH_WEST"
          ]
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "SOUTH"
          ]
          value [
            type "enum"
            name "org.arakhne.neteditor.fig.figure.ResizeDirection"
            value "SOUTH_EAST"
          ]
        ]
        text "condition"
        uuid [
          type "uuid"
          value "d045d5af-b7fa-46a7-9225-01785deed89d"
        ]
        viewuuid [
          type "uuid"
          value "7207b0f8-3e55-4da2-a016-f919b3086320"
        ]
        width 55.482421875
        x 348.6342468261719
        y 164.34266662597656
      ]
    ]
  ]
  Figure [
    viewid "7207b0f8-3e55-4da2-a016-f919b3086320"
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fig.figure.decoration.RectangleFigure"
    uuid "0c8fe70f-b275-4c8f-a1c6-cae2f561c90c"
    attributes [
      filled [
        type "boolean"
        value 1
      ]
      framed [
        type "boolean"
        value 1
      ]
      height 62.0
      isAutoLockAssociatedFigures [
        type "boolean"
        value 1
      ]
      islocked [
        type "boolean"
        value 0
      ]
      isselectable [
        type "boolean"
        value 1
      ]
      maxheight 3.4028234663852886E38
      maxwidth 3.4028234663852886E38
      minheight 40.0
      minwidth 40.0
      resizedirections [
        type "set"
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_EAST"
        ]
      ]
      uuid [
        type "uuid"
        value "0c8fe70f-b275-4c8f-a1c6-cae2f561c90c"
      ]
      viewuuid [
        type "uuid"
        value "7207b0f8-3e55-4da2-a016-f919b3086320"
      ]
      width 75.0
      x 243.5
      y 165.0
    ]
  ]
  Figure [
    viewid "7207b0f8-3e55-4da2-a016-f919b3086320"
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.figures.FSMStateFigure"
    uuid "10f73b33-0d58-4894-a7d7-4195cf1fd5eb"
    modelid "2"
    attributes [
      arcSize 10.0
      height 40.0
      isAutoLockAssociatedFigures [
        type "boolean"
        value 1
      ]
      islocked [
        type "boolean"
        value 0
      ]
      isselectable [
        type "boolean"
        value 1
      ]
      maxheight 3.4028234663852886E38
      maxwidth 3.4028234663852886E38
      minheight 40.0
      minwidth 40.0
      name "2"
      resizedirections [
        type "set"
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_EAST"
        ]
      ]
      uuid [
        type "uuid"
        value "10f73b33-0d58-4894-a7d7-4195cf1fd5eb"
      ]
      viewuuid [
        type "uuid"
        value "7207b0f8-3e55-4da2-a016-f919b3086320"
      ]
      width 40.0
      x 480.5
      y 147.0
    ]
    Figure [
      type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fig.anchor.InvisibleRoundRectangularAnchorFigure"
      uuid "9a793e60-e930-4f2d-a876-836caf461c71"
      modelid "3"
      attributes [
        arcSize 10.0
        height 40.0
        maxheight 3.4028234663852886E38
        maxwidth 3.4028234663852886E38
        minheight 0.0
        minwidth 0.0
        uuid [
          type "uuid"
          value "9a793e60-e930-4f2d-a876-836caf461c71"
        ]
        viewuuid [
          type "uuid"
          value "7207b0f8-3e55-4da2-a016-f919b3086320"
        ]
        width 40.0
        x 0.0
        y 0.0
      ]
    ]
  ]
  Figure [
    viewid "7207b0f8-3e55-4da2-a016-f919b3086320"
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fsm.figures.FSMStateFigure"
    uuid "b1ff2d8c-02d9-4c04-bfcf-fc5a5b813bd3"
    modelid "0"
    attributes [
      arcSize 10.0
      height 40.0
      isAutoLockAssociatedFigures [
        type "boolean"
        value 1
      ]
      islocked [
        type "boolean"
        value 0
      ]
      isselectable [
        type "boolean"
        value 1
      ]
      maxheight 3.4028234663852886E38
      maxwidth 3.4028234663852886E38
      minheight 40.0
      minwidth 40.0
      name "1"
      resizedirections [
        type "set"
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_EAST"
        ]
      ]
      uuid [
        type "uuid"
        value "b1ff2d8c-02d9-4c04-bfcf-fc5a5b813bd3"
      ]
      viewuuid [
        type "uuid"
        value "7207b0f8-3e55-4da2-a016-f919b3086320"
      ]
      width 40.0
      x 235.39797973632812
      y 216.9697723388672
    ]
    Figure [
      type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fig.anchor.InvisibleRoundRectangularAnchorFigure"
      uuid "082828b5-fcf3-4670-9bf1-e7d2e96d8adc"
      modelid "1"
      attributes [
        arcSize 10.0
        height 40.0
        maxheight 3.4028234663852886E38
        maxwidth 3.4028234663852886E38
        minheight 0.0
        minwidth 0.0
        uuid [
          type "uuid"
          value "082828b5-fcf3-4670-9bf1-e7d2e96d8adc"
        ]
        viewuuid [
          type "uuid"
          value "7207b0f8-3e55-4da2-a016-f919b3086320"
        ]
        width 40.0
        x 0.0
        y 0.0
      ]
    ]
  ]
  Figure [
    viewid "7207b0f8-3e55-4da2-a016-f919b3086320"
    type "http://www.arakhne.org/neteditor/generic.gml#org.arakhne.neteditor.fig.figure.coercion.CoercedTextFigure"
    uuid "d045d5af-b7fa-46a7-9225-01785deed89d"
    attributes [
      anchorDescription 0.5
      anchorX 390.3814392089844
      anchorY 178.13558959960938
      dx -14.0059814453125
      dy -6.8085479736328125
      filled [
        type "boolean"
        value 0
      ]
      framed [
        type "boolean"
        value 0
      ]
      height 13.96875
      isAutoLockAssociatedFigures [
        type "boolean"
        value 1
      ]
      islocked [
        type "boolean"
        value 0
      ]
      isselectable [
        type "boolean"
        value 1
      ]
      maxheight 3.4028234663852886E38
      maxwidth 3.4028234663852886E38
      minheight 1.0
      minwidth 1.0
      resizedirections [
        type "set"
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "NORTH_EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "EAST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_WEST"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH"
        ]
        value [
          type "enum"
          name "org.arakhne.neteditor.fig.figure.ResizeDirection"
          value "SOUTH_EAST"
        ]
      ]
      text "condition"
      uuid [
        type "uuid"
        value "d045d5af-b7fa-46a7-9225-01785deed89d"
      ]
      viewuuid [
        type "uuid"
        value "7207b0f8-3e55-4da2-a016-f919b3086320"
      ]
      width 55.482421875
      x 348.6342468261719
      y 164.34266662597656
    ]
  ]
]
