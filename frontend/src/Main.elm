module Main exposing (..)

import Browser
import Element exposing (..)
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Element.Input as Input
import Http
import Json.Decode as Decode
import Json.Encode as Encode



-- MODEL


type alias Model =
    { name : String
    , response : String
    , loading : Bool
    }


init : () -> ( Model, Cmd Msg )
init _ =
    ( { name = "", response = "", loading = False }, Cmd.none )



-- UPDATE


type Msg
    = UpdateName String
    | PostHello
    | GetHello
    | GotResponse (Result Http.Error String)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        UpdateName name ->
            ( { model | name = name }, Cmd.none )

        PostHello ->
            ( { model | loading = True }, postHelloRequest model.name )

        GetHello ->
            ( { model | loading = True }, getHello )

        GotResponse result ->
            case result of
                Ok message ->
                    ( { model | response = message, loading = False }, Cmd.none )

                Err error ->
                    ( { model | response = "Error: " ++ httpErrorString error, loading = False }, Cmd.none )



-- HTTP


postHelloRequest : String -> Cmd Msg
postHelloRequest name =
    Http.post
        { url = "http://localhost:8888/api/hello"
        , body = Http.jsonBody (Encode.object [ ( "name", Encode.string name ) ])
        , expect = Http.expectJson GotResponse responseDecoder
        }


getHello : Cmd Msg
getHello =
    Http.get
        { url = "http://localhost:8888/api/hello"
        , expect = Http.expectJson GotResponse responseDecoder
        }


responseDecoder : Decode.Decoder String
responseDecoder =
    Decode.field "message" Decode.string


httpErrorString : Http.Error -> String
httpErrorString error =
    case error of
        Http.BadUrl url ->
            "Bad URL: " ++ url

        Http.Timeout ->
            "Request timeout"

        Http.NetworkError ->
            "Network error - check if backend is running on localhost:8888"

        Http.BadStatus status ->
            "Bad status: " ++ String.fromInt status

        Http.BadBody body ->
            "Bad body: " ++ body



-- VIEW


view : Model -> Element Msg
view model =
    column
        [ padding 10
        , spacing 15 -- spacing is like margin
        , centerX
        , width (px 600)

        -- , centerY
        ]
        [ -- h1
          el [ Font.size 20, Font.bold, centerX ] (text "Notes App")
        , -- POST input and button row
          row [ spacing 10, centerX ]
            [ Input.text
                [ padding 5
                , width (px 400)
                , height (px 40)
                ]
                { onChange = UpdateName
                , text = model.name
                , placeholder = Just (Input.placeholder [] (text "Write a new note here :)"))
                , label = Input.labelHidden "Name"
                }
            , Input.button
                [ Background.color (rgb255 140 120 222)
                , Font.color (rgb255 0 0 0)
                , padding 10
                , Border.rounded 3
                , if model.loading || String.isEmpty model.name then
                    alpha 0.5

                  else
                    alpha 1.0
                ]
                { onPress =
                    if model.loading || String.isEmpty model.name then
                        Nothing

                    else
                        Just PostHello
                , label = text "Add"
                }
            ]
        , -- Response area
          row [centerX]
            [ if model.loading then
                el [ Font.color (rgb255 233 215 246) ] (text "Loading...")

              else
                el
                    [ padding 10
                    , Border.solid
                    , Border.width 1
                    , Border.rounded 3
                    , Border.color (rgb255 204 204 255)
                    , Font.color (rgb255 210 23 104)
                    , width (px 470)
                    ]
                    (text ("Response: " ++ model.response))
            ]
        ]



-- MAIN - slightly different!


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , view = \model -> layout [] (view model) -- Wrap view in layout
        , subscriptions = \_ -> Sub.none
        }
